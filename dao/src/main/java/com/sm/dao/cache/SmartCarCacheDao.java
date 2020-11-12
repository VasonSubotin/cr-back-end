package com.sm.dao.cache;

import com.sm.model.SmartCarCache;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface SmartCarCacheDao {
    SmartCarCache getSmartCarCache(String externalResourceId);

    List<SmartCarCache> getSmartCarCacheIn(Collection<String> externalResourceIds);

    @Transactional(readOnly = false)
    void saveSmartCarCache(SmartCarCache smartCarCache);
}
