package com.sm.client.services;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.sm.client.utils.JwtTokenUtil;
import com.sm.dao.conf.Constants;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

@Service
public class GoogleService {

    @Value("${google.clientId:513874746428-663v507o7i96n3p5vnm2l1e2tf2ldm09.apps.googleusercontent.com}")
    private String clientId;

    @Value("${google.clientSecret:80-cBgW-v5EDVFVBEIzsXtew}")
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
                Arrays.asList("https://www.googleapis.com/auth/calendar",
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
        authorizationCodeTokenRequest.setRedirectUri("http://localhost:8080/googleToken");

        return authorizationCodeTokenRequest.execute();
    }

    public Calendar getCalendar() throws SmException {
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
        SmUserSession smUserSession = securityService.getActiveSession(Constants.GOOGLE_AUTH_TYPE);
        credential.setAccessToken(smUserSession.getToken());
        return new Calendar.Builder(authorizationCodeFlow.getTransport(), authorizationCodeFlow.getJsonFactory(), credential).build();
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