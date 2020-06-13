package com.sm.client.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, CustomUserDetails> userCache = new HashMap<>();

    @PostConstruct
    public void init() {
        //some test users
        userCache.put("test1", new CustomUserDetails("test1", passwordEncoder.encode("12345"), Arrays.asList(new Permision("test-role"))));
        userCache.put("test2", new CustomUserDetails("test2", passwordEncoder.encode("12345"), Arrays.asList(new Permision("test-role"))));
        userCache.put("test3", new CustomUserDetails("test3", passwordEncoder.encode("12345"), Arrays.asList(new Permision("test-role"))));
        userCache.put("test4", new CustomUserDetails("test4", passwordEncoder.encode("12345"), Arrays.asList(new Permision("test-role"))));
    }

    public UserDetails loadUserByUsername(String username) {
        return userCache.get(username);
    }

    public void registerUser(String username, String password) {
        userCache.put(username, new CustomUserDetails(username, passwordEncoder.encode(password), Arrays.asList(new Permision("test-role"))));
    }

    public static class CustomUserDetails implements UserDetails {
        private String user;
        private String password;
        private List<Permision> permissions;

        public CustomUserDetails(String user, String password, List<Permision> permissions) {
            this.user = user;
            this.password = password;
            this.permissions = permissions;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return permissions;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return user;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    public static class Permision implements GrantedAuthority {

        private String authority;

        public Permision(String authority) {
            this.authority = authority;
        }

        @Override
        public String getAuthority() {
            return authority;
        }
    }
}
