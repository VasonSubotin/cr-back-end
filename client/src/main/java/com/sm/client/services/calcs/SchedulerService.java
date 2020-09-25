package com.sm.client.services.calcs;

import com.sm.client.model.smartcar.SchedulerData;
import com.sm.model.SmScheduleType;

import java.util.Date;
import java.util.List;

public interface SchedulerService {
    //  SchedulerData calculateSchedule(Long resourceId, boolean geo) throws Exception;

    SchedulerData calculateDrivingScheduleGeo(Long resourceId) throws Exception;

    SchedulerData calculateDrivingSchedule(Long resourceId) throws Exception;

    SchedulerData calculateCharingSchedule(Long resourceId) throws Exception;

    SchedulerData getLastSchdule(String login, Long resourceId, SmScheduleType type) throws Exception;

    List<SchedulerData> getSchduleHistory(String login, Long resourceId, Date start, Date stop, SmScheduleType type) throws Exception;

    SchedulerData saveSchdule(SchedulerData schedulerData, Long accountId) throws Exception;
}
