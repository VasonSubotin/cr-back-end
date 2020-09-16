package com.sm.client.services.calcs;

import com.sm.client.model.smartcar.SchedulerData;

import java.util.Date;
import java.util.List;

public interface SchedulerService {
    //  SchedulerData calculateSchedule(Long resourceId, boolean geo) throws Exception;

    SchedulerData calculateDrivingScheduleGeo(Long resourceId) throws Exception;

    SchedulerData calculateDrivingSchedule(Long resourceId) throws Exception;

    SchedulerData calculateCharingSchedule(Long resourceId) throws Exception;

    SchedulerData getLastSchdule(String login, Long resourceId) throws Exception;

    List<SchedulerData> getSchduleHistory(String login, Long resourceId, Date start, Date stop) throws Exception;

    SchedulerData saveSchdule(SchedulerData schedulerData, Long accountId) throws Exception;
}
