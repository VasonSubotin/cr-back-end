package com.sm.dao;

import com.sm.model.SmUserSession;

import java.util.List;

public interface UserSessionDao {
    List<SmUserSession> getActiveSessions(Long accountId);

    List<SmUserSession> getActiveSessionsByType(Long accountId, String userSessionType);

    SmUserSession saveSession(SmUserSession smUserSession);
}
