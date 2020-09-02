package com.sm.client.services;

import com.sm.model.SmException;
import com.sm.model.SmTimeOfUsage;

import java.util.List;

public interface TimeOfUsageService {
    SmTimeOfUsage getTimeOfUsageById(Long touId) throws SmException;

    SmTimeOfUsage deleteTimeOfUsageById(Long touId) throws SmException;

    SmTimeOfUsage getTimeOfUsageByResourceId(Long resourceId) throws SmException;

    List<SmTimeOfUsage> getAllPersonalTimeOfUsages() throws SmException;

    SmTimeOfUsage saveTimeOfUsageByResourceId(Long resourceId, SmTimeOfUsage smTimeOfUsage) throws SmException;

    SmTimeOfUsage updateTimeOfUsageByResourceId(Long resource, SmTimeOfUsage smTimeOfUsage) throws SmException;

    SmTimeOfUsage updateTimeOfUsage(Long touId, SmTimeOfUsage smTimeOfUsage) throws SmException;
}
