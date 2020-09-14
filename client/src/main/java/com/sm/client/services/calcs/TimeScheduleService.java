package com.sm.client.services.calcs;

import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.model.SmResource;
import com.sm.model.SmUserSession;

import java.util.Date;

public interface TimeScheduleService {
    SchedulerData calculateSchedule(VehicleData smData, SmResource smResource, Date starttime, Date endtime) throws Exception;
}
