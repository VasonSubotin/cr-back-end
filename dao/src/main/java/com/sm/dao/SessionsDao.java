package com.sm.dao;

import com.sm.model.SmEvent;
import com.sm.model.SmSession;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface SessionsDao {
    SmSession getActiveSessionByAccountIdAndResourceId(Long accountId, Long resourceId);


    List<SmSession> getAllSessionsByAccountIdAndResourceIdAndTimeRange(Long accountId, Long resourceId, Date start, Date stop, Long limit);

    @javax.transaction.Transactional
    void saveSession(SmSession session);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    void updateSession(SmSession session);
}
