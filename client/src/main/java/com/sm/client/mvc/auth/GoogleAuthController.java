package com.sm.client.mvc.auth;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.sm.client.services.SecurityService;
import com.sm.dao.conf.Constants;
import com.sm.model.ServiceResult;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@RestController
public class GoogleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);


    @Value("${google.clientId:513874746428-663v507o7i96n3p5vnm2l1e2tf2ldm09.apps.googleusercontent.com}")
    private String clientId;

    @Value("${google.clientSecret:80-cBgW-v5EDVFVBEIzsXtew}")
    private String clientSecret;

    private static String authURI = "https://accounts.google.com/o/oauth2/auth";

    private AuthorizationCodeFlow authorizationCodeFlow;

    @Autowired
    private SecurityService securityService;

    @PostConstruct
    public void init() {
        this.authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                clientId, clientSecret,
                Collections.singleton("https://www.googleapis.com/auth/calendar")).setAccessType("offline").build();

    }


    @RequestMapping(value = "/googleLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String googleLogin(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        logger.info("----------------------call/googleLogin -------------------");

        AuthorizationCodeRequestUrl url = authorizationCodeFlow.newAuthorizationUrl();
        url.setRedirectUri("http://localhost:8080/googleToken");
        String link = url.build();
        //String link = client.getAuthUrl();
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", link);
        return link;
    }


    @RequestMapping(value = "/googleToken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String googleCode(HttpServletRequest request, HttpServletResponse response, String code) {
        logger.info("----------------------call googleToken -------------------");
        return code;
    }

    @RequestMapping(value = "/googleSession", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(HttpServletRequest request, HttpServletResponse response, String code) throws IOException {
        try {
            // request
            AuthorizationCodeTokenRequest authorizationCodeTokenRequest = authorizationCodeFlow.newTokenRequest(code);
            authorizationCodeTokenRequest.setRedirectUri("http://localhost:8080/googleToken");
            TokenResponse tokenResponse = authorizationCodeTokenRequest.execute();
            String token = tokenResponse.getAccessToken();
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
