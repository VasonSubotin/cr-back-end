package com.sm.client.model.smartcar;

import java.io.Serializable;
import java.util.List;

public class UserData implements Serializable {
    private List<VehicleData> vehiclesList;
    private String userId;

    public List<VehicleData> getVehiclesList() {
        return vehiclesList;
    }

    public void setVehiclesList(List<VehicleData> vehiclesList) {
        this.vehiclesList = vehiclesList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
