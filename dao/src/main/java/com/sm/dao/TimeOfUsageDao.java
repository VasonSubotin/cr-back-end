package com.sm.dao;

import com.sm.model.SmTimeOfUsage;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TimeOfUsageDao {
    List<SmTimeOfUsage> getAllSmTimeOfUsages();

    List<SmTimeOfUsage> getTimeOfUsagesByResourceIn(List<Long> resourcesId);

    SmTimeOfUsage getTimeOfUsageById(Long id);

    SmTimeOfUsage getTimeOfUsageByResourceId(Long resourceId);

    @Transactional(readOnly = false)
    SmTimeOfUsage saveTimeOfUsage(SmTimeOfUsage smTimeOfUsage);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    SmTimeOfUsage updateTimeOfUsage(SmTimeOfUsage smTimeOfUsage);

    @Transactional(readOnly = false)
    SmTimeOfUsage deleteTimeOfUsageById(Long id);
}
