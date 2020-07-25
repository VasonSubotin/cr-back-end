package com.sm.client.services;

import com.sm.model.SmException;
import com.sm.model.web.LocationScheduleItem;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface LocationScheduleService {
    List<LocationScheduleItem> calculate(Long accountId, Long resourceId, double maxRadius, Date start, Date stop) throws SmException, IOException;
}
