package com.sm.dao;

import com.sm.model.SmSession;

import java.util.Date;
import java.util.List;

public interface SessionsDao {
    List<SmSession> getActiveSessionsByAccountIdAndResourceId(Long accountId, Long resourceId);

    List<SmSession> getAllSessionsByAccountIdAndResourceIdAndTimeRange(Long accountId, Long resourceId, Date start, Date stop, Long limit);
}
