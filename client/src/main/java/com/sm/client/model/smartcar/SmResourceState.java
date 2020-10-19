package com.sm.client.model.smartcar;

import com.sm.model.SmResource;

import java.io.Serializable;

public class SmResourceState implements Serializable {

    private VehicleData smartCarInfo;
    private SmResource smResource;

    public SmResourceState() {

    }

    public SmResourceState(VehicleData smartCarInfo, SmResource smResource) {
        this.smartCarInfo = smartCarInfo;
        this.smResource = smResource;
    }

    public VehicleData getSmartCarInfo() {
        return smartCarInfo;
    }

    public void setSmartCarInfo(VehicleData smartCarInfo) {
        this.smartCarInfo = smartCarInfo;
    }

    public SmResource getSmResource() {
        return smResource;
    }

    public void setSmResource(SmResource smResource) {
        this.smResource = smResource;
    }
}

