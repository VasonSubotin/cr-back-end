package com.sm.client.mvc;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Date;

@RestController
public class GoogleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);


    @Value("${google.clientId:513874746428-663v507o7i96n3p5vnm2l1e2tf2ldm09.apps.googleusercontent.com}")
    private String clientId;

    @Value("${google.clientSecret:80-cBgW-v5EDVFVBEIzsXtew}")
    private String clientSecret;

    private static String authURI = "https://accounts.google.com/o/oauth2/auth";

    private String token;

    private AuthorizationCodeFlow authorizationCodeFlow;


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
    public String googleCode(HttpServletRequest request,
                             HttpServletResponse response, String code) throws Exception {
        logger.info("----------------------call/googleToken -------------------");

        // request
        AuthorizationCodeTokenRequest authorizationCodeTokenRequest = authorizationCodeFlow.newTokenRequest(code);
        authorizationCodeTokenRequest.setRedirectUri("http://localhost:8080/googleToken");

        TokenResponse tokenResponse = authorizationCodeTokenRequest.execute();
        token = tokenResponse.getAccessToken();
        return token;
    }


    @RequestMapping(value = "/getCalendarEvent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCalendarEvent(HttpServletRequest request,
                                   HttpServletResponse response, String code) throws Exception {
        logger.info("----------------------call/googleToken -------------------");
        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
        credential.setAccessToken(token);
        Calendar service = new Calendar.Builder(authorizationCodeFlow.getTransport(), authorizationCodeFlow.getJsonFactory(), credential).build();

        return service.events().list("primary").setMaxResults(10).execute().toString();
    }


}
