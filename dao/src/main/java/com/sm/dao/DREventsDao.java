package com.sm.dao;

import com.sm.model.SmDREvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DREventsDao {
    List<SmDREvent> getAllDREvents();

    List<SmDREvent> getDREventsByResourceId(Long resourceId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    SmDREvent saveOrUpdateDREvent(SmDREvent smDREvent);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    void saveOrUpdateDREvents(List<SmDREvent> smDREvents);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    void deleteOrUpdateDREvents(Long resourceId);
}
