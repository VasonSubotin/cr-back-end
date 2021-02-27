package com.sm.dao;

import com.sm.model.SmLocation;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.util.List;

public interface LocationDao {
    List<SmLocation> getAllLocations();

    List<SmLocation> getAllPersonalLocations(Long accountId);

    SmLocation getLocationByIdAndAccountId(Long id, Long accountId);

    SmLocation getLocationByExternalIdAndAccountId(String externalLocationId, Long accountId);

    List<SmLocation> getLocationsInSmallRangeAndAccountId(Long accountId, double latitudeA, double longitudeA, double latitudeB, double longitudeB);

    SmLocation saveLocation(SmLocation smLocation, Long accountId);

    @org.springframework.transaction.annotation.Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    SmLocation updateLocation(SmLocation smLocation, Long accountId);

    @Transactional
    SmLocation deleteLocationById(Long id, Long accountId) ;
}
