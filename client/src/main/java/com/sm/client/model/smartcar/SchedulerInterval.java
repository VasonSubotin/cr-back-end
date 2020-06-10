package com.sm.client.model.smartcar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class SchedulerInterval implements Serializable {
    @JsonProperty("duration")
    private long duration;

    @JsonProperty("charge_rate")
    private long chargeRate;

    @JsonProperty("primary_trigger")
    private String primaryTrigger;

    @JsonProperty("interval_type")
    private String intervalType;

    @JsonProperty("economic_impact")
    private double economicImpact;

    @JsonProperty("co2_impact")
    private double co2Impact;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
    @JsonProperty("starttime")
    private Date starttime;

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

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public double getEconomicImpact() {
        return economicImpact;
    }

    public void setEconomicImpact(double economicImpact) {
        this.economicImpact = economicImpact;
    }

    public double getCo2Impact() {
        return co2Impact;
    }

    public void setCo2Impact(double co2Impact) {
        this.co2Impact = co2Impact;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }
}
