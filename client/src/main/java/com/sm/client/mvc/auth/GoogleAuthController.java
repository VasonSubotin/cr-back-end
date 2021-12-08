package com.sm.client.mvc.auth;

import com.sm.client.model.AuthResponse;
import com.sm.client.services.GoogleService;
import com.sm.client.services.SecurityService;
import com.sm.client.utils.JwtTokenUtil;
import com.sm.model.Constants;
import com.sm.model.ServiceResult;
import com.sm.model.SmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GoogleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthController.class);

    @Autowired
    private GoogleService googleService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/googleReLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleReLogin(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestParam(name = "redirect", required = false) String redirect) throws Exception {
        logger.info("----------------------call/googleLogin -------------------");
        return googleService.redirectToGoogleRenew(redirect);
    }


    @RequestMapping(value = "/googleLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleLogin(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestParam(name = "redirect", required = false) String redirect) throws Exception {
        logger.info("----------------------call/googleLogin -------------------");
        //return googleService.redirectToGoogle(redirect);
        return googleService.redirectToGoogleRenew(redirect);
    }

    @RequestMapping(value = "/googleToken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleCode(HttpServletRequest request, HttpServletResponse response, String code) {
        logger.info("----------------------call googleToken -------------------");
        return ResponseEntity.ok(code);
    }

    @RequestMapping(value = "/googleAuthenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleSessionAuthG(HttpServletRequest request, HttpServletResponse response, String code, @RequestParam(name = "redirect", required = false) String redirect
    ) throws Exception {
        return googleSessionAuth(request, response, code, redirect);
    }

    @RequestMapping(value = "/googleAuthenticate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleSessionAuth(HttpServletRequest request, HttpServletResponse response, String code, @RequestParam(name = "redirect", required = false) String redirect
    ) throws Exception {
        try {
            String token = googleService.googleSessionAuth(code);
            if (token == null) {
                return googleReLogin(request, response, redirect);
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

        return googleService.getCalendar(securityService.getActiveSession(Constants.GOOGLE_AUTH_TYPE).get(0)).events().list("primary").setMaxResults(10).execute().toString();
    }

    @RequestMapping(value = "/needInitGoogleSession", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> needInitGoogleSession(HttpServletRequest request, HttpServletResponse response) {
        try {

            Map<String, Object> res = new HashMap<>();
            res.put("needInit", googleService.needInitGoogleSession());
            return new ResponseEntity(res, HttpStatus.OK);
        } catch (SmException ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.valueOf(ex.getCode()));
        }
    }

}
