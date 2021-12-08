package com.sm.client.services.optimization;

import com.sm.model.PolicyType;
import com.sm.client.model.smartcar.SchedulerData;

import java.util.Date;
import java.util.TimeZone;

public interface OptimizationService {

    SchedulerData optimize(Date start,
                           Date stop,
                           long capacityInWt,
                           long chargeLevelInWt,
                           long rateInWt,
                           String locationId,
                           TimeZone timeZone,
                           Long resourceId) throws Exception;

    PolicyType getPolicy();
}
