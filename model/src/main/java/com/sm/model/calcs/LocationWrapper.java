package com.sm.model.calcs;

import com.sm.model.Constants;
import com.sm.model.SmLocation;

public class LocationWrapper {
    private SmLocation smLocation;
    private Double distance;
    private Double priceRate; //
    private long energy; // need energy

    public LocationWrapper(SmLocation smLocation, Double distance) {
        this.smLocation = smLocation;
        this.distance = distance;
        this.priceRate = smLocation.getPrice() / (smLocation.getPower() == null ? Constants.DEFAULT_POWER_WATT : smLocation.getPower());
    }

    public SmLocation getSmLocation() {
        return smLocation;
    }

    public void setSmLocation(SmLocation smLocation) {
        this.smLocation = smLocation;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(Double priceRate) {
        this.priceRate = priceRate;
    }

    public long getEnergy() {
        return energy;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }
}
