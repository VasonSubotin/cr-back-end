package com.sm.client.model.smartcar;

import com.smartcar.sdk.data.*;

import java.io.Serializable;

public class VehicleData implements Serializable {

    private String vin;
    private String vehicleId;

    private SmartcarResponse<VehicleCharge> charge;
    private VehicleBattery battery;
    private SmartcarResponse<VehicleOdometer> odometer;
    private SmartcarResponse<VehicleFuel> fuel;
    private SmartcarResponse<VehicleOil> oil;
    private VehicleInfo vehicleInfo;
    private SmartcarResponse<VehicleTirePressure> tirePressure;

    private SmartcarResponse<VehicleLocation> location;


    public VehicleInfo getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(VehicleInfo vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public VehicleBattery getBattery() {
        return battery;
    }

    public void setBattery(VehicleBattery battery) {
        this.battery = battery;
    }

    public SmartcarResponse<VehicleOdometer> getOdometer() {
        return odometer;
    }

    public void setOdometer(SmartcarResponse<VehicleOdometer> odometer) {
        this.odometer = odometer;
    }

    public SmartcarResponse<VehicleCharge> getCharge() {
        return charge;
    }

    public void setCharge(SmartcarResponse<VehicleCharge> charge) {
        this.charge = charge;
    }

    public SmartcarResponse<VehicleFuel> getFuel() {
        return fuel;
    }

    public void setFuel(SmartcarResponse<VehicleFuel> fuel) {
        this.fuel = fuel;
    }

    public SmartcarResponse<VehicleOil> getOil() {
        return oil;
    }

    public void setOil(SmartcarResponse<VehicleOil> oil) {
        this.oil = oil;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public SmartcarResponse<VehicleLocation> getLocation() {
        return location;
    }

    public void setLocation(SmartcarResponse<VehicleLocation> location) {
        this.location = location;
    }

    public SmartcarResponse<VehicleTirePressure> getTirePressure() {
        return tirePressure;
    }

    public void setTirePressure(SmartcarResponse<VehicleTirePressure> tirePressure) {
        this.tirePressure = tirePressure;
    }
}

