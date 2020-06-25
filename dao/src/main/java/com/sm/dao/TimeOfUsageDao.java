package com.sm.dao;

import com.sm.model.SmTimeOfUsage;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TimeOfUsageDao {
    List<SmTimeOfUsage> getAllSmTimeOfUsages();

    List<SmTimeOfUsage> getAllPersonalTimeOfUsages(Long accountId);

    SmTimeOfUsage getTimeOfUsageByIdAndAccountId(Long id, Long accountId);

    @Transactional(readOnly = false)
    SmTimeOfUsage saveTimeOfUsage(SmTimeOfUsage smTimeOfUsage, Long accountId);

    @Transactional(readOnly = false)
    SmTimeOfUsage deleteTimeOfUsageById(Long id, Long accountId);
}
