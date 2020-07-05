package com.sm.client.services;

import com.sm.model.SmAccount;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;

public interface SecurityService {
    SmAccount getAccount() throws SmException;

    SmUserSession getActiveSession(String sessionType) throws SmException;

    SmUserSession getActiveSessionByLogin(String sessionType, String login) throws SmException;

    SmUserSession saveCurrentSession(String sessionType, String token, String refreshToken, long ttl) throws SmException;

    SmUserSession saveCurrentSessionByLogin(String sessionType, String token, String refreshToken, long ttl, String login) throws SmException;
}
