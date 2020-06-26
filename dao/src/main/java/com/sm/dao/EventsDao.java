package com.sm.dao;

import com.sm.model.SmEvent;

import java.util.Date;
import java.util.List;

public interface  EventsDao {
    SmEvent getLastEventByAccountIdAndResourceId(Long accountId, Long resourceId);

    List<SmEvent> getAllEventsByAccountIdAndResourceIdAndTimeRange(Long accountId, Long resourceId, Date start, Date stop, Long limit);
}
