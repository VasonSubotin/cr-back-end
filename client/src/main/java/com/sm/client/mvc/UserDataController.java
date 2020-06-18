package com.sm.client.mvc;


import com.sm.client.services.UserDetailsServiceImpl;
import com.sm.model.PolicyType;
import com.sm.model.SmResource;
import com.sm.model.SmUser;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
        smUser.setUserId(1);
        smUser.setAuthLink("https://www.google.com/login");
        smUser.setAuthType(SmUser.AuthType.LOCAL);
        smUser.setLogin(userDetails.getUsername());
        smUser.setPassHash(userDetails.getPassword().getBytes());
       // smUser.setPermissions(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return smUser;
    }


    @RequestMapping(value = "/getUserResources", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmResource> getUserResources(HttpServletRequest request) throws Exception {
        return Arrays.asList(generateMock(PolicyType.ECO), generateMock(PolicyType.ECO_PRICE), generateMock(PolicyType.SIMPLE), generateMock(PolicyType.PRICE), generateMock(PolicyType.PRICE));
    }

    private SmResource generateMock(PolicyType policyType) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        SmResource resource1 = new SmResource();
        resource1.setAccountId(username);
        resource1.setPolicyType(policyType);
        resource1.setResourceId(UUID.randomUUID().toString().toUpperCase());
        return resource1;
    }
}
