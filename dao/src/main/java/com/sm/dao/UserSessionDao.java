package com.sm.dao;

import com.sm.model.SmUserSession;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface UserSessionDao {
    List<SmUserSession> getAllSessions();

    List<SmUserSession> getActiveSessions(Long accountId);


    List<SmUserSession> getSessionsByToken(Long accountId, String token);

    List<SmUserSession> getSessionsByType(Long accountId, String userSessionType);

    SmUserSession getSessionsByTypeAndRecourceId(Long accountId, String userSessionType, Long resourceId);

    SmUserSession saveSession(SmUserSession smUserSession);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    SmUserSession updateSession(SmUserSession smUserSession);

    @Transactional(readOnly = false)
    void deleteSession(Long accountId, Long resourceId, String userSessionType);

    Collection<SmUserSession> saveSessions(Collection<SmUserSession> smUserSession);
}
