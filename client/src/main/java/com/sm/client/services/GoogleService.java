package com.sm.client.services;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequest;
import com.google.api.services.calendar.model.Events;
import com.sm.client.utils.JwtTokenUtil;

import com.sm.model.Constants;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleService {
    private Logger logger = LoggerFactory.getLogger(GoogleService.class);

    @Value("${google.clientId:#{null}}")
    private String clientId;

    @Value("${google.clientSecret:#{null}}")
    private String clientSecret;

    @Value("${google.redirectUrl:http://localhost:8080/googleToken}")
    private String urlRedirect;

    private AuthorizationCodeFlow authorizationRefreshCodeFlow;
    private AuthorizationCodeFlow authorizationCodeFlow;
    private GoogleIdTokenVerifier googleIdTokenVerifier;


    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SecurityService securityService;

    @PostConstruct
    public void init() {
        this.authorizationRefreshCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                clientId, clientSecret,
                Arrays.asList("https://www.googleapis.com/auth/calendar", "https://www.googleapis.com/auth/calendar.events.readonly",
                        "openid", "https://www.googleapis.com/auth/userinfo.email")).setApprovalPrompt("force").setAccessType("offline").build();

        this.authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                clientId, clientSecret,
                Arrays.asList("https://www.googleapis.com/auth/calendar",
                        "openid", "https://www.googleapis.com/auth/userinfo.email")).setAccessType("offline").build();

        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();

    }


    public TokenResponse refreshToken(String refreshToken) throws IOException {
        TokenResponse tokenResponse = new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                refreshToken, clientId, clientSecret).setScopes(
                Arrays.asList("https://www.googleapis.com/auth/calendar", "openid",
                        "https://www.googleapis.com/auth/userinfo.email")).setGrantType("refresh_token").execute();

        return tokenResponse;
    }

    public ResponseEntity<?> redirectToGoogleRenew() {
        AuthorizationCodeRequestUrl url = authorizationRefreshCodeFlow.newAuthorizationUrl();
        logger.debug("-- setting redirection for google to {}", urlRedirect);
        url.setRedirectUri(urlRedirect);
        String link = url.build();
//        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
//        response.setHeader("Location", link);
        MultiValueMap header = new HttpHeaders();
        header.add("Location", link);
        return new ResponseEntity(null, header, HttpStatus.MOVED_PERMANENTLY);
        // return link;
    }

    public ResponseEntity<?> redirectToGoogle() {
        AuthorizationCodeRequestUrl url = authorizationCodeFlow.newAuthorizationUrl();
        logger.debug("-- setting redirection for google to {}", urlRedirect);
        url.setRedirectUri(urlRedirect);
        String link = url.build();
        //String link = client.getAuthUrl();
//        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
//        response.setHeader("Location", link);
        //return link;
        MultiValueMap header = new HttpHeaders();
        header.add("Location", link);
        return new ResponseEntity(null, header, HttpStatus.MOVED_PERMANENTLY);
    }

    public TokenResponse getToken(String code) throws IOException {
        // request
        AuthorizationCodeTokenRequest authorizationCodeTokenRequest = authorizationCodeFlow.newTokenRequest(code);
        logger.debug("-- setting redirection for google to {}", urlRedirect);
        authorizationCodeTokenRequest.setRedirectUri(urlRedirect);

        return authorizationCodeTokenRequest.execute();
    }

    public Calendar getCalendar( SmUserSession smUserSession ) throws SmException {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());

        credential.setAccessToken(smUserSession.getToken());
        return new Calendar.Builder(authorizationCodeFlow.getTransport(), authorizationCodeFlow.getJsonFactory(), credential).build();
    }

//    public Events getEvents(int maxResult) throws SmException, IOException {
//        SmUserSession smUserSession = securityService.getActiveSession(Constants.GOOGLE_AUTH_TYPE);
//        if (smUserSession == null) {
//            throw new SmException("No active google session found for login " + SecurityContextHolder.getContext().getAuthentication().getName(), org.apache.http.HttpStatus.SC_FORBIDDEN);
//        }
//        return getCalendar(smUserSession).events().list("primary").setMaxResults(maxResult).execute();
//    }

    public Events getEventsForPeriodInMills(Long from, long timeRangeInMills) throws SmException, IOException {
        long current = from == null ? System.currentTimeMillis() : from;
        long end = current + timeRangeInMills;

        SmUserSession smUserSession = securityService.getActiveSession(Constants.GOOGLE_AUTH_TYPE);
        if (smUserSession == null) {
            throw new SmException("No active google session found for login " + SecurityContextHolder.getContext().getAuthentication().getName(), org.apache.http.HttpStatus.SC_FORBIDDEN);
        }

        try {
            return getCalendar(smUserSession).events().list("primary").setTimeMin(new DateTime(current)).setTimeMax(new DateTime(end)).execute();
        } catch (GoogleJsonResponseException ex) {
            // need to refresh token
            TokenResponse tokenResponse = refreshToken(smUserSession.getRefreshToken());
            smUserSession = securityService.saveCurrentSession(Constants.GOOGLE_AUTH_TYPE, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), tokenResponse.getExpiresInSeconds() * 1000L);
            return getCalendar(smUserSession).events().list("primary").setTimeMin(new DateTime(current)).setTimeMax(new DateTime(end)).execute();
        }
    }

    private final long time24hours = 24 * 3600 * 1000;

    public Events getEventsForNext24hours() throws SmException, IOException {
        return getEventsForPeriodInMills(null, time24hours);
    }

    public void stratSession(String code) throws SmException, IOException {
        TokenResponse tokenResponse = getToken(code);
        securityService.saveCurrentSession(Constants.GOOGLE_AUTH_TYPE, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), tokenResponse.getExpiresInSeconds() * 1000L);
    }

    public String googleSessionAuth(String code) throws SmException, GeneralSecurityException, IOException {
        TokenResponse tokenResponse = getToken(code);

        String idToken = ((GoogleTokenResponse) tokenResponse).getIdToken();
        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
        String login = googleIdToken.getPayload().getEmail();

        final UserDetails userDetails = userDetailsService.check3dPartyAuthrization(login, Constants.GOOGLE_AUTH_TYPE);
        final String token = jwtTokenUtil.generateToken(userDetails);

        //creating google session
        SmUserSession smUserSession = securityService.saveCurrentSessionByLogin(
                Constants.GOOGLE_AUTH_TYPE,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                tokenResponse.getExpiresInSeconds() * 1000L,
                login);
        if (smUserSession.getRefreshToken() == null) {
            //need to rerequest with relogin
            return null;
        }

        return token;
    }
}
