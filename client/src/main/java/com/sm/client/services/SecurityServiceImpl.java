package com.sm.client.services;

import com.sm.dao.AccountsDao;
import com.sm.model.SmAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private AccountsDao accountsDao;


    @Override
    public SmAccount getAccount() throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        SmAccount smAccount = accountsDao.getAccountByLogin(login);
        if (smAccount == null) {
            throw new Exception("User " + login + " is not registred in system - internal error?");
        }
        return smAccount;
    }
}
