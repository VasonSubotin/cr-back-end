package com.sm.client.services.calcs;

import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.model.SmResource;
import com.sm.model.SmUserSession;

public interface TimeScheduleService {
    SchedulerData calculateSchedule(VehicleData smData, SmResource smResource, String starttime, String endtime) throws Exception;
}
