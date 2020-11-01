package com.sm.dao.cache;

import com.sm.model.SmartCarCache;
import org.springframework.transaction.annotation.Transactional;

public interface SmartCarCacheDao {
    SmartCarCache getSmartCarCache(String externalResourceId);

    @Transactional(readOnly = false)
    void saveSmartCarCache(SmartCarCache smartCarCache);
}
