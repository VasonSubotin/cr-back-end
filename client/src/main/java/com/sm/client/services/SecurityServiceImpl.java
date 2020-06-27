package com.sm.client.services;

import com.sm.dao.AccountsDao;
import com.sm.dao.UserSessionDao;
import com.sm.model.SmAccount;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private UserSessionDao userSessionDao;

    @Override
    public SmAccount getAccount() throws SmException {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        SmAccount smAccount = accountsDao.getAccountByLogin(login);
        if (smAccount == null) {
            throw new SmException("User " + login + " is not registred in system - internal error?", HttpStatus.SC_NOT_FOUND);
        }
        return smAccount;
    }

    @Override
    public SmUserSession getActiveSession(String sessionType) throws SmException {
        SmAccount smAccount = getAccount();

        List<SmUserSession> smUserSessions = userSessionDao.getActiveSessionsByType(smAccount.getIdAccount(), sessionType);
        if (smUserSessions == null) {
            return null;
        }

        SmUserSession sessionResult = null;
        //checking sessions
        for (SmUserSession smUserSession : smUserSessions) {
            //checking expiration
            if (smUserSession.getDtCreated() == null || smUserSession.getDtCreated().getTime() + smUserSession.getTtl() < System.currentTimeMillis()) {
                //session has been exipred or does not have date - we need to close it
                smUserSession.setClosed(true);
                userSessionDao.saveSession(smUserSession);
                sessionResult = sessionResult == null ? smUserSession : sessionResult;
            } else {
                sessionResult = smUserSession;
                // we will contiue to fix all other records if any
            }
        }

        return sessionResult;
    }

    @Override
    public SmUserSession saveCurrentSession(String sessionType, String token, String refreshToken, long ttl) throws SmException {
        SmUserSession smUserSession = new SmUserSession();
        smUserSession.setClosed(false);
        smUserSession.setAccountId(getAccount().getIdAccount());
        smUserSession.setToken(token);
        smUserSession.setRefreshToken(refreshToken);
        smUserSession.setSessionType(sessionType);
        smUserSession.setTtl(ttl);
        smUserSession.setDtCreated(new Date());
        return userSessionDao.saveSession(smUserSession);
    }
}
