package com.sm.client.services;

import com.sm.model.SmDREvent;
import com.sm.model.SmException;

import java.util.List;

public interface DREventService {
    List<SmDREvent> getDREventsByResourceId(Long resourceId) throws SmException;
}
