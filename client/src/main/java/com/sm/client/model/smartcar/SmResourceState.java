package com.sm.client.model.smartcar;

import com.sm.model.SmResource;
import com.sm.model.SmTiming;

import java.io.Serializable;
import java.util.List;

public class SmResourceState implements Serializable {

    private VehicleData smartCarInfo;
    private SmResource smResource;
    private List<SmTiming> timers;

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

    public List<SmTiming> getTimers() {
        return timers;
    }

    public void setTimers(List<SmTiming> timers) {
        this.timers = timers;
    }
}

