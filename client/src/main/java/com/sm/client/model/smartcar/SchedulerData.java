package com.sm.client.model.smartcar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SchedulerData implements Serializable {

    @JsonProperty("schedule_id")
    private String schedulerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    @JsonProperty("time_start")
    private Date timeStart;

    @JsonProperty("co2_summary")
    private double co2_summary;

    @JsonProperty("co2_savings")
    private double co2_savings;

    @JsonProperty("Intervals")
    private List<SchedulerInterval> intervals;

    public String getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(String schedulerId) {
        this.schedulerId = schedulerId;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public double getCo2_summary() {
        return co2_summary;
    }

    public void setCo2_summary(double co2_summary) {
        this.co2_summary = co2_summary;
    }

    public double getCo2_savings() {
        return co2_savings;
    }

    public void setCo2_savings(double co2_savings) {
        this.co2_savings = co2_savings;
    }

    public List<SchedulerInterval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<SchedulerInterval> intervals) {
        this.intervals = intervals;
    }
}
