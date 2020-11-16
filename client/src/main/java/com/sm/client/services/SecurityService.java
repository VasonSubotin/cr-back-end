package com.sm.client.services;

import com.sm.model.SmAccount;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;

import java.util.Collection;
import java.util.List;

public interface SecurityService {
    SmAccount getAccount() throws SmException;

    List<SmUserSession> getActiveSession(String sessionType) throws SmException;

   // Collection<SmUserSession> getActiveSessionByToken(String token) throws SmException;

    SmUserSession getActiveSession(String sessionType, Long resourceId) throws SmException;

    SmUserSession getActiveSessionByLogin(String sessionType, String login, Long resourceId) throws SmException;

    List<SmUserSession> getActiveSessionByLogin(String sessionType, String login) throws SmException;

    SmUserSession updateCurrentSession(String oldToken, String sessionType, String token, String refreshToken, long ttl) throws SmException;

   // SmUserSession saveCurrentSession(String oldToken, String sessionType, String token, String refreshToken, long ttl) throws SmException;

    SmUserSession saveCurrentSessionByLogin(String sessionType, String token, String refreshToken, long ttl, String login) throws SmException;

    SmUserSession createSmUserSession(String sessionType, String token, String refreshToken, long ttl, Long idAccount);
}
