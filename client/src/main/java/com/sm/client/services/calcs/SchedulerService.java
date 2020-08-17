package com.sm.client.services.calcs;

import com.sm.client.model.smartcar.SchedulerData;

public interface SchedulerService {
    SchedulerData calculateSchedule(Long resourceId, boolean geo) throws Exception;

    SchedulerData getLastSchdule(String login, Long resourceId) throws Exception;
}
