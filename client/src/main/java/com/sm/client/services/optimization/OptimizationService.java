package com.sm.client.services.optimization;

import com.sm.model.PolicyType;
import com.sm.client.model.smartcar.SchedulerData;

import java.util.Date;

public interface OptimizationService {

    SchedulerData optimize(Date start,
                           Date stop,
                           long capacityInWt,
                           long chargeLevelInWt,
                           long rateInWt,
                           String locationId,
                           Long resourceId) throws Exception;

    PolicyType getPolicy();
}
