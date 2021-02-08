package com.sm.client.services;

import com.sm.client.model.smartcar.SmResourceState;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import com.sm.model.SmUserSession;
import com.smartcar.sdk.SmartcarException;

import java.util.List;

public interface ResourceService {
    SmResourceState getResourceState(Long resourceId) throws SmException;

    List<SmResourceState> getResourceStates() throws SmException;

    String getResourceImage(Long resourceId) throws SmException;

    void refreshCarData(SmUserSession smUserSession) throws SmException, SmartcarException;
}
