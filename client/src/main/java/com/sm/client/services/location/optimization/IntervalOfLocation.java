package com.sm.client.services.location.optimization;

public class IntervalOfLocation {
    private double summaryPrice; // will be calculated
    private long minEnergyLeft;// minimum level of battary when it reach the next point
    private long needEnergy; // minimum energy required to reach the next location
    private long charge; // will be calculated
    private double price; // price rate - cost of 1 watt, will be multiplicate to charge
    private Object refObject; // object which is connected to this interval ( can be location or event or something else)

    public IntervalOfLocation() {
    }

    public IntervalOfLocation(long minEnergyLeft, long needEnergy, double price, Object refObject) {
        this.minEnergyLeft = minEnergyLeft;
        this.needEnergy = needEnergy;
        this.price = price;
        this.refObject = refObject;
    }

    public long getNeedEnergy() {
        return needEnergy;
    }

    public void setNeedEnergy(long needEnergy) {
        this.needEnergy = needEnergy;
    }

    public long getCharge() {
        return charge;
    }

    public void setCharge(long charge) {
        this.charge = charge;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Object getRefObject() {
        return refObject;
    }

    public void setRefObject(Object refObject) {
        this.refObject = refObject;
    }

    public long getMinEnergyLeft() {
        return minEnergyLeft;
    }

    public void setMinEnergyLeft(long minEnergyLeft) {
        this.minEnergyLeft = minEnergyLeft;
    }

    public double getSummaryPrice() {
        return summaryPrice;
    }

    public void setSummaryPrice(double summaryPrice) {
        this.summaryPrice = summaryPrice;
    }
}
