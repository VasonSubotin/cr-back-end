package com.sm.dao;

import com.sm.model.*;

import java.util.List;

public interface CommonDao {
    List<SmPolicy> getAllPoliciesByActive(boolean active);

    List<SmPolicy> getAllPolicies();

    List<SmResourceType> getAllResourceTypes();

    List<SmSessionType> getAllSessionTypes();

    List<SmEventType> getAllEventTypes();

    List<VehicleModel> getVehicles();
}
