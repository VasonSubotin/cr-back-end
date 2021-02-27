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

import java.util.Collection;
import java.util.Collections;
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
    public List<SmUserSession> getActiveSession(String sessionType) throws SmException {
        return userSessionDao.getSessionsByType(getAccount().getIdAccount(), sessionType);
    }

    @Override
    public SmUserSession getActiveSession(String sessionType, Long resourceId) throws SmException {
        return userSessionDao.getSessionsByTypeAndRecourceId(getAccount().getIdAccount(), sessionType, resourceId);
    }

    @Override
    public SmUserSession getActiveSessionByLogin(String sessionType, String login, Long resourceId) throws SmException {
        SmAccount smAccount = accountsDao.getAccountByLogin(login);
        return userSessionDao.getSessionsByTypeAndRecourceId(smAccount.getIdAccount(), sessionType, resourceId);
    }

    @Override
    public List<SmUserSession> getActiveSessionByLogin(String sessionType, String login) throws SmException {
        SmAccount smAccount = accountsDao.getAccountByLogin(login);
        return userSessionDao.getSessionsByType(smAccount.getIdAccount(), sessionType);
    }


    @Override
    public SmUserSession updateCurrentSession(String oldToken, String sessionType, String token, String refreshToken, long ttl) throws SmException {
        //checking the last session
        Long accountId = getAccount().getIdAccount();
        List<SmUserSession> existsSessions = userSessionDao.getSessionsByToken(accountId, oldToken);
        if (refreshToken == null && existsSessions != null && !existsSessions.isEmpty()) {
            refreshToken = existsSessions.iterator().next().getRefreshToken();
        }

        SmUserSession ret = new SmUserSession();
        // return saveSession(sessionType, token, refreshToken, ttl, getAccount().getIdAccount());
        for (SmUserSession smUserSession : existsSessions) {
            smUserSession.setToken(token);
            smUserSession.setRefreshToken(refreshToken);
            smUserSession.setTtl(ttl);
            userSessionDao.updateSession(smUserSession);
            ret = smUserSession;
        }

        return ret;
    }


    @Override
    public SmUserSession saveCurrentSessionByLogin(String sessionType, String token, String refreshToken, long ttl, String login) throws SmException {
        //checking the last session
        SmAccount smAccount = accountsDao.getAccountByLogin(login);
        List<SmUserSession> existsSessions = userSessionDao.getSessionsByType(smAccount.getIdAccount(), sessionType);

        if (refreshToken == null && existsSessions != null && !existsSessions.isEmpty()) {
            refreshToken = existsSessions.get(0).getRefreshToken();
        }
        return saveSession(sessionType, token, refreshToken, ttl, smAccount.getIdAccount());
    }

    private SmUserSession saveSession(String sessionType, String token, String refreshToken, long ttl, Long idAccount) {
        // userSessionDao.getLastSessionsByType()
        //we need to find the last one and close
        SmUserSession smUserSession = new SmUserSession();
        smUserSession.setClosed(false);
        smUserSession.setAccountId(idAccount);
        smUserSession.setToken(token);
        smUserSession.setRefreshToken(refreshToken);
        smUserSession.setSessionType(sessionType);
        smUserSession.setTtl(ttl);
        smUserSession.setDtCreated(new Date());
        return userSessionDao.saveSession(smUserSession);
    }

    @Override
    public SmUserSession createSmUserSession(String sessionType, String token, String refreshToken, long ttl, Long idAccount) {
        SmUserSession smUserSession = new SmUserSession();
        smUserSession.setClosed(false);
        smUserSession.setAccountId(idAccount);
        smUserSession.setToken(token);
        smUserSession.setRefreshToken(refreshToken);
        smUserSession.setSessionType(sessionType);
        smUserSession.setTtl(ttl);
        smUserSession.setDtCreated(new Date());
        return smUserSession;
    }

    @Override
    public void deleteSession(String sessionType, Long resourceId) throws SmException {
        userSessionDao.deleteSession(getAccount().getIdAccount(), resourceId, sessionType);
    }
}
