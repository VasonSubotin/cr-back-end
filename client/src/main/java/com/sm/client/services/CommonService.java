package com.sm.client.services;

import com.sm.model.SmartCarCache;
import com.sm.model.web.RecourseInfo;

import java.util.Collection;
import java.util.List;

public interface CommonService {
    RecourseInfo getRecourseInfo(Long accountId, Long resourceId);

    SmartCarCache getSmartCarCache(String vin);

    List<SmartCarCache> getSmartCarCacheIn(Collection<String> vin);
}
