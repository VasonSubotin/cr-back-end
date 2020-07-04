package com.sm.dao;

import com.sm.model.SmUserSession;

import java.util.List;

public interface UserSessionDao {
    List<SmUserSession> getAllSessions();

    List<SmUserSession> getActiveSessions(Long accountId);

    SmUserSession getLastSessionsByType(Long accountId, String userSessionType);

    SmUserSession saveSession(SmUserSession smUserSession);
}
