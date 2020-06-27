package com.sm.client.services;

import com.sm.model.SmAccount;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;

public interface SecurityService {
    SmAccount getAccount() throws Exception;

    SmUserSession getActiveSession(String sessionType) throws SmException;

    SmUserSession saveCurrentSession(String sessionType, String token, String refreshToken, long ttl) throws SmException;
}
