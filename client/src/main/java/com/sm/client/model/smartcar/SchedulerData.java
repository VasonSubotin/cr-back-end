package com.sm.client.model.smartcar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SchedulerData implements Serializable {

    @JsonProperty("schedule_id")
    private Long schedulerId;

    @JsonProperty("session_id")
    private Long sessionId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("policy_id")
    private Long policyId;

    @JsonProperty("resource_id")
    private Long resourceId;

    @JsonProperty("location_id")
    private Long locationId;

    // can be flying, driving, changing
    @JsonProperty("session_type")
    private String sessionType;

    @JsonProperty("co2_impact")
    private double co2Impact;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    @JsonProperty("start_time")
    private Date timeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    @JsonProperty("end_time")
    private Date timeStop;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ssZ")
    @JsonProperty("create_time")
    private Date createdTime;

    @JsonProperty("co2_savings")
    private double co2_savings;

    @JsonProperty("monetary_savings")
    private double financeSavings;

    @JsonProperty("intervals")
    private List<SchedulerInterval> intervals;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(Long schedulerId) {
        this.schedulerId = schedulerId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public double getCo2Impact() {
        return co2Impact;
    }

    public void setCo2Impact(double co2Impact) {
        this.co2Impact = co2Impact;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public double getFinanceSavings() {
        return financeSavings;
    }

    public void setFinanceSavings(double financeSavings) {
        this.financeSavings = financeSavings;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeStop() {
        return timeStop;
    }

    public void setTimeStop(Date timeStop) {
        this.timeStop = timeStop;
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

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
