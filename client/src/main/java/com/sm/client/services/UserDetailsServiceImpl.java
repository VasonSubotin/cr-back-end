package com.sm.client.services;

import com.sm.dao.AccountsDao;

import com.sm.model.SmAccount;
import com.sm.model.SmException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private AccountsDao accountsDao;


    public UserDetails loadUserByUsername(String username) {
        SmAccount smAccount = accountsDao.getAccountByLogin(username);
        return new CustomUserDetails(username, smAccount.getPassword(), Arrays.asList(new Permision("test-role")));
    }

    public UserDetails check3dPartyAuthrization(String username, String authrizationType) {
        SmAccount smAccount = accountsDao.getAccountByLogin(username);
        if (smAccount == null) {
            //need to add new 3d party client
            smAccount = new SmAccount();
            smAccount.setLogin(username);
            smAccount.setDtCreated(new Date());
            smAccount.setPassword("***********");
            accountsDao.saveAccount(smAccount);
        }

        return new CustomUserDetails(username, smAccount.getPassword(), Arrays.asList(new Permision("test-role")));
    }

    public void registerUser(SmAccount smAccount) throws SmException {
        SmAccount exists = accountsDao.getAccountByLogin(smAccount.getLogin());
        if (exists != null) {
            throw new SmException("User " + smAccount.getLogin() + " already exists!", HttpStatus.SC_CONFLICT);
        }
        smAccount.setDtCreated(new Date());
        smAccount.setDeleted(false);
        //encrypting pass
        smAccount.setPassword(passwordEncoder.encode(smAccount.getPassword()));
        accountsDao.saveAccount(smAccount);
    }

    public SmAccount updateUser(SmAccount smAccount) throws SmException {
        SmAccount exists = accountsDao.getAccountByLogin(smAccount.getLogin());
        if (exists == null) {
            throw new SmException("User " + smAccount.getLogin() + " does not exist!", HttpStatus.SC_NOT_FOUND);
        }
        smAccount.setDtCreated(new Date());
        smAccount.setDeleted(false);

//        if (smAccount.getLogin() != null || !smAccount.getLogin().isEmpty()) {
//            exists.setLogin(smAccount.getLogin());
//        }

        if (smAccount.getEmail() != null && !smAccount.getEmail().isEmpty()) {
            exists.setEmail(smAccount.getEmail());
        }

        if (smAccount.getFirstName() != null && !smAccount.getFirstName().isEmpty()) {
            exists.setFirstName(smAccount.getFirstName());
        }

        if (smAccount.getLastName() != null && !smAccount.getLastName().isEmpty()) {
            exists.setLastName(smAccount.getLastName());
        }

        //encrypting pass
        if (smAccount.getPassword() != null && !smAccount.getPassword().isEmpty()) {
            exists.setPassword(passwordEncoder.encode(smAccount.getPassword()));
        }
        //return accountsDao.saveAccount(exists);
        return accountsDao.updateAccount(exists);
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
