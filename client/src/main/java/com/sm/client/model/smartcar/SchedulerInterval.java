package com.sm.client.model.smartcar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SchedulerInterval implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    @JsonProperty("time_start")
    private Date startTime;

    @JsonProperty("calendar_location")
    private LocationPoint eventLocation;

    @JsonProperty("station_locations")
    private List<LocationPoint> stations;

    @JsonProperty("duration")
    private long duration;

    @JsonProperty("power")
    private long chargeRate;

    //energy for this interval
    @JsonProperty("energy")
    private long energy;

    // global battery level
    @JsonProperty("soc_achieved")
    private long soc;

    // notes for RTM, DAM, frequency  regulation, will be used for time schedule
    @JsonProperty("primary_trigger")
    private String primaryTrigger;

    // type of interval , can be NCHR, CHR,DRV
    @JsonProperty("interval_type")
    private IntervalType intervalType;

    @JsonProperty("co2_impact")
    private double co2Impact;

    @JsonProperty("price")
    private double price;

    // cost of charging for this interval
    @JsonProperty("cost_of_charging")
    private double costOfCharging;

    // economic savings on the interval  for location scheduler
    @JsonProperty("economic_savings")
    private double economicSavings;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getChargeRate() {
        return chargeRate;
    }

    public void setChargeRate(long chargeRate) {
        this.chargeRate = chargeRate;
    }

    public String getPrimaryTrigger() {
        return primaryTrigger;
    }

    public void setPrimaryTrigger(String primaryTrigger) {
        this.primaryTrigger = primaryTrigger;
    }

    public IntervalType getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(IntervalType intervalType) {
        this.intervalType = intervalType;
    }

    public double getCo2Impact() {
        return co2Impact;
    }

    public void setCo2Impact(double co2Impact) {
        this.co2Impact = co2Impact;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public LocationPoint getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(LocationPoint eventLocation) {
        this.eventLocation = eventLocation;
    }

    public List<LocationPoint> getStations() {
        return stations;
    }

    public void setStations(List<LocationPoint> stations) {
        this.stations = stations;
    }

    public long getEnergy() {
        return energy;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }

    public long getSoc() {
        return soc;
    }

    public void setSoc(long soc) {
        this.soc = soc;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCostOfCharging() {
        return costOfCharging;
    }

    public void setCostOfCharging(double costOfCharging) {
        this.costOfCharging = costOfCharging;
    }

    public double getEconomicSavings() {
        return economicSavings;
    }

    public void setEconomicSavings(double economicSavings) {
        this.economicSavings = economicSavings;
    }

    public enum IntervalType {
        DRV("Driving to the location"),
        FLG("Flying to the location"),
        NCHR("Connected to charge but not charging"),
        CHR("Charging");


        private String description;

        IntervalType(String description) {
            this.description = description;
        }
    }
}
