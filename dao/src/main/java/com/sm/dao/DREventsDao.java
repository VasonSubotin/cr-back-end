package com.sm.dao;

import com.sm.model.SmDREvent;

import java.util.List;

public interface DREventsDao {
    List<SmDREvent> getAllDREvents();

    List<SmDREvent> getDREventsByResourceId(Long resourceId);
}
