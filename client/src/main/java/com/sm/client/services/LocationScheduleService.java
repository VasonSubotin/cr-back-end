package com.sm.client.services;

import com.sm.client.model.smartcar.SchedulerData;
import com.sm.model.SmException;

import java.io.IOException;
import java.util.Date;

public interface LocationScheduleService {
    SchedulerData calculate(Long accountId, Long resourceId, double maxRadius, Date start, Date stop) throws SmException, IOException;
}
