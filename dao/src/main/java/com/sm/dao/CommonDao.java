package com.sm.dao;

import com.sm.model.SmPolicy;
import com.sm.model.SmResourceType;
import com.sm.model.SmSessionType;

import java.util.List;

public interface CommonDao {
    List<SmPolicy> getAllPoliciesByActive(boolean active);

    List<SmPolicy> getAllPolicies();

    List<SmResourceType> getAllResourceTypes();

    List<SmSessionType> getAllSessionTypes();
}
