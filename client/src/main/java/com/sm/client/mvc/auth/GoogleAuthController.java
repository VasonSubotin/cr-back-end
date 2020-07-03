package com.sm.client.mvc.auth;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.sm.client.model.AuthResponse;
import com.sm.client.services.SecurityService;
import com.sm.client.services.UserDetailsServiceImpl;
import com.sm.client.utils.JwtTokenUtil;
import com.sm.dao.conf.Constants;
import com.sm.model.ServiceResult;
import com.sm.model.SmAccount;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

@RestController
public class GoogleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);


    @Value("${google.clientId:513874746428-663v507o7i96n3p5vnm2l1e2tf2ldm09.apps.googleusercontent.com}")
    private String clientId;

    @Value("${google.clientSecret:80-cBgW-v5EDVFVBEIzsXtew}")
    private String clientSecret;

    @Value("${google.redirectUrl:http://localhost:8080/googleToken}")
    private String urlRedirect;

    private AuthorizationCodeFlow authorizationCodeFlow;

    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostConstruct
    public void init() {
        this.authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                clientId, clientSecret,
                Arrays.asList("https://www.googleapis.com/auth/calendar", "openid", "https://www.googleapis.com/auth/userinfo.email")).setAccessType("offline").build();

        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(clientId))
                .build();


    }

    @RequestMapping(value = "/googleAuthenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String googleAuthenticate(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        logger.info("----------------------call/googleAuthenticate -------------------");
        return redirectToGoogle(response);
    }


    @RequestMapping(value = "/googleLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String googleLogin(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        logger.info("----------------------call/googleLogin -------------------");
        return redirectToGoogle(response);
    }

    private String redirectToGoogle(HttpServletResponse response) {
        AuthorizationCodeRequestUrl url = authorizationCodeFlow.newAuthorizationUrl();
        url.setRedirectUri(urlRedirect);
        String link = url.build();
        //String link = client.getAuthUrl();
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", link);
        return link;
    }

    private TokenResponse getToken(String code) throws IOException {
        // request
        AuthorizationCodeTokenRequest authorizationCodeTokenRequest = authorizationCodeFlow.newTokenRequest(code);
        authorizationCodeTokenRequest.setRedirectUri("http://localhost:8080/googleToken");

        return authorizationCodeTokenRequest.execute();
    }

    @RequestMapping(value = "/googleToken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String googleCode(HttpServletRequest request, HttpServletResponse response, String code) {
        logger.info("----------------------call googleToken -------------------");
        return code;
    }

    @RequestMapping(value = "/googleAuthenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleSessionAuth(HttpServletRequest request, HttpServletResponse response, String code) throws IOException, GeneralSecurityException {
        try {
            TokenResponse tokenResponse = getToken(code);

            String idToken = ((GoogleTokenResponse) tokenResponse).getIdToken();
            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
            String login = googleIdToken.getPayload().getEmail();

            final UserDetails userDetails = userDetailsService.check3dPartyAuthrization(login, Constants.GOOGLE_AUTH_TYPE);
            final String token = jwtTokenUtil.generateToken(userDetails);

            //creating google session
            securityService.saveCurrentSession(Constants.GOOGLE_AUTH_TYPE, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), tokenResponse.getExpiresInSeconds() * 1000L);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/authrized"), status);
        }
    }

    @RequestMapping(value = "/googleSession", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(HttpServletRequest request, HttpServletResponse response, String code) throws IOException, GeneralSecurityException {
        try {
            TokenResponse tokenResponse = getToken(code);
            securityService.saveCurrentSession(Constants.GOOGLE_AUTH_TYPE, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), tokenResponse.getExpiresInSeconds() * 1000L);
            return new ResponseEntity(HttpStatus.OK);
        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/authrized"), status);
        }
    }

    @RequestMapping(value = "/getCalendarEvent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCalendarEvent(HttpServletRequest request,
                                   HttpServletResponse response, String code) throws Exception {
        logger.info("----------------------call/googleToken -------------------");
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
        SmUserSession smUserSession = securityService.getActiveSession(Constants.GOOGLE_AUTH_TYPE);
        credential.setAccessToken(smUserSession.getToken());
        Calendar service = new Calendar.Builder(authorizationCodeFlow.getTransport(), authorizationCodeFlow.getJsonFactory(), credential).build();


        return service.events().list("primary").setMaxResults(10).execute().toString();
    }


}
