package com.sm.client.mvc;


import com.sm.client.services.UserDetailsServiceImpl;
import com.sm.model.SmUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;


@RestController
public class UserDataController {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @RequestMapping(value = "/getUserProfile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmUser getEvents(
            HttpServletRequest request
    ) throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        SmUser smUser = new SmUser();
        smUser.setUserName("Mr " + username);
        smUser.setUserId(username + "-" + username);
        smUser.setAuthLink("https://www.google.com/login");
        smUser.setAuthType(SmUser.AuthType.LOCAL);
        smUser.setLogin(userDetails.getUsername());
        smUser.setPassHash(userDetails.getPassword().getBytes());
        smUser.setPermissions(userDetails.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
        return smUser;
    }
}
