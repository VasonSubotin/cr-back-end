package com.sm.client.services;

import com.sm.model.SmartCarCache;
import com.sm.model.web.RecourseInfo;

public interface CommonService {
    RecourseInfo getRecourseInfo(Long accountId, Long resourceId);

    SmartCarCache getSmartCarCache(String vin);
}
