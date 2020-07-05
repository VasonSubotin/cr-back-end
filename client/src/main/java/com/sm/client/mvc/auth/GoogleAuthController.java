package com.sm.client.mvc.auth;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.sm.client.model.AuthResponse;
import com.sm.client.services.GoogleService;
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

    @Autowired
    private GoogleService googleService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @RequestMapping(value = "/googleReLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleReLogin(HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        logger.info("----------------------call/googleLogin -------------------");
        return googleService.redirectToGoogleRenew();
    }


    @RequestMapping(value = "/googleLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleLogin(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        logger.info("----------------------call/googleLogin -------------------");
       return googleService.redirectToGoogle();
    }

    @RequestMapping(value = "/googleToken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleCode(HttpServletRequest request, HttpServletResponse response, String code) {
        logger.info("----------------------call googleToken -------------------");
        return ResponseEntity.ok(code);
    }

    @RequestMapping(value = "/googleAuthenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleSessionAuth(HttpServletRequest request, HttpServletResponse response, String code) throws Exception {
        try {
            String token = googleService.googleSessionAuth(code);
            if (token == null) {
                return googleReLogin(request, response);
                //return new ResponseEntity (response.get);
            }
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/googleAuthenticate"), status);
        }
    }

    @RequestMapping(value = "/googleSession", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> get(HttpServletRequest request, HttpServletResponse response, String code) throws IOException, GeneralSecurityException {
        try {
            googleService.stratSession(code);
            return new ResponseEntity(HttpStatus.OK);
        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/googleSession"), status);
        }
    }

    @RequestMapping(value = "/getCalendarEvent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCalendarEvent(HttpServletRequest request, HttpServletResponse response, String code) throws Exception {
        logger.info("----------------------call/getCalendarEvent -------------------");
        return googleService.getCalendar().events().list("primary").setMaxResults(10).execute().toString();
    }


}
