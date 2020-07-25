package com.sm.dao.cache;

import com.sm.model.cache.Coordinates;
import org.springframework.transaction.annotation.Transactional;

public interface CoordinatesCacheDao {
    @Transactional(readOnly = false)
    void saveCoordinates(Coordinates coordinates);

    Coordinates loadCoordinates(String address);
}
