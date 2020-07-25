package com.sm.dao;

import com.sm.model.SmLocation;

import javax.transaction.Transactional;
import java.util.List;

public interface LocationDao {
    List<SmLocation> getAllLocations();

    List<SmLocation> getAllPersonalLocations(Long accountId);

    SmLocation getLocationByIdAndAccountId(Long id, Long accountId);

    List<SmLocation> getLocationsInSmallRangeAndAccountId(Long accountId, double latitudeA, double longitudeA, double latitudeB, double longitudeB);

    SmLocation saveLocation(SmLocation smLocation, Long accountId);

    @Transactional
    SmLocation deleteLocationById(Long id, Long accountId) ;
}
