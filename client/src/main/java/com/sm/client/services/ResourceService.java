package com.sm.client.services;

import com.sm.client.model.smartcar.SmResourceState;
import com.sm.model.SmException;

import java.util.List;

public interface ResourceService {
    SmResourceState getResourceState(Long resourceId) throws SmException;

    List<SmResourceState> getResourceStates() throws SmException;

    String getResourceImage(Long resourceId) throws SmException;
}
