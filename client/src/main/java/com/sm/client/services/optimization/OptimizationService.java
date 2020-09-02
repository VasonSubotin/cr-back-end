package com.sm.client.services.optimization;

import com.sm.model.PolicyType;
import com.sm.client.model.smartcar.SchedulerData;

public interface OptimizationService {

    SchedulerData optimize(String start,
                           String stop,
                           long capacityInWt,
                           long chargeLevelInWt,
                           long rateInWt,
                           String locationId,
                           Long resourceId) throws Exception;

    PolicyType getPolicy();
}
