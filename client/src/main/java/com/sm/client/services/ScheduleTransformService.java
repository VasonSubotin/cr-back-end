package com.sm.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.model.SmSchedules;

import java.io.IOException;

public interface ScheduleTransformService {
    SmSchedules scheduleWebToSmSchedules(SchedulerData schedulerData) throws JsonProcessingException;

    SchedulerData smSchedulesToScheduleWeb(SmSchedules smSchedules) throws IOException;
}
