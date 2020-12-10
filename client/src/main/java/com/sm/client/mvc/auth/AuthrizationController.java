package com.sm.client.mvc.auth;


import java.util.Date;
import java.util.Objects;

import com.sm.client.model.AuthRequest;
import com.sm.client.model.AuthResponse;
import com.sm.client.model.JwtTokenRequest;
import com.sm.client.services.UserDetailsServiceImpl;
import com.sm.client.services.cache.SmartCarCacheService;
import com.sm.client.utils.JwtTokenUtil;
import com.sm.model.ServiceResult;
import com.sm.model.SmAccount;
import com.sm.model.SmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
public class AuthrizationController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SmartCarCacheService smartCarCacheService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SmAccount authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getLogin(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getLogin());

        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            smartCarCacheService.refreshOnBackground(username);
            smartCarCacheService.addLoginForUpdate(username);
        } catch (DisabledException e) {
            throw new Exception("User is disabled", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid username or password", e);
        }
    }


    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signup(@RequestBody SmAccount authenticationRequest) throws Exception {
        try {
            String password = authenticationRequest.getPassword();
            userDetailsService.registerUser(authenticationRequest);
            SmAccount authRequest = new SmAccount();
            authRequest.setPassword(password);
            authRequest.setLogin(authenticationRequest.getLogin());
            return createAuthenticationToken(authRequest);
        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/signup"), status);
        }
    }

    @RequestMapping(value = "/getJwtToken", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getJwtToken(@RequestBody(required = false) JwtTokenRequest jwtTokenRequest,
                                         @RequestParam(value = "expirationInSec", required = false) Long expirationInSec) throws Exception {
        try {
            return new ResponseEntity("{ \"token\":\"" + jwtTokenUtil.generateHS256(jwtTokenRequest, expirationInSec) + "\"}", HttpStatus.CREATED);
        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/getJwtToken"), status);
        }
    }
}