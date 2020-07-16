package com.sm.client.services;

import com.sm.client.model.smartcar.SchedulerData;
import com.sm.model.SmResource;
import com.sm.model.SmUserSession;

public interface ScheduleService {
    SchedulerData calculateSchedule(String login, Long resourceId, String starttime, String endtime) throws Exception;

    SchedulerData getLastSchdule(String login, Long resourceId) throws Exception;
}
