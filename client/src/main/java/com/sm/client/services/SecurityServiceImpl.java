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
        return userSessionDao.getLastSessionsByType(getAccount().getIdAccount(), sessionType);
    }

    //method to optimize sessions
    private void fixCLoseSession() {
        List<SmUserSession> smUserSessions = userSessionDao.getAllSessions();

        //checking sessions
        for (SmUserSession smUserSession : smUserSessions) {
            //checking expiration
            if (smUserSession.getDtCreated() == null || smUserSession.getDtCreated().getTime() + smUserSession.getTtl() < System.currentTimeMillis()) {
                //session has been exipred or does not have date - we need to close it
                smUserSession.setClosed(true);
                userSessionDao.saveSession(smUserSession);
            }
        }
    }

    @Override
    public SmUserSession saveCurrentSession(String sessionType, String token, String refreshToken, long ttl) throws SmException {
        //checking the last session
        SmUserSession existsSession = getActiveSession(sessionType);
        if (refreshToken == null && existsSession != null) {
            refreshToken = existsSession.getRefreshToken();
        }
        //we need to find the last one and close
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
