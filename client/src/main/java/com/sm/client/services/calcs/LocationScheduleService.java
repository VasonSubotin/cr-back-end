package com.sm.client.services.calcs;

import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.model.SmException;
import com.sm.model.SmResource;

import java.io.IOException;
import java.util.Date;

public interface LocationScheduleService {
    SchedulerData calculate(Long accountId, VehicleData smData, SmResource smResource) throws SmException, IOException;

    SchedulerData calculateGeo(Long accountId, VehicleData smData, SmResource smResource) throws SmException, IOException;
}
