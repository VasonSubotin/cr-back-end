package com.sm.client.model.smartcar;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sm.client.model.eco.GridData;
import com.sm.client.model.eco.GridDataAggregated;
import com.sm.model.SmScheduleType;

import java.io.Serializable;
import java.util.ArrayList;
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

    @JsonProperty("initial_energy")
    private Long initialEnergy;

    @JsonProperty("end_soc")
    private Double endSoc;

    @JsonProperty("capacity")
    private Long capacity;

    @JsonProperty("total_charge")
    private Long totalEnergy;

    @JsonProperty("total_cost")
    private Double totalCost;

    // can be flying, driving, changing
    @JsonProperty("session_type")
    private String sessionType;

    @JsonProperty("schedule_type")
    private SmScheduleType scheduleType;

    @JsonProperty("co2_impact")
    private double co2Impact;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("start_time")
    private Date timeStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("end_time")
    private Date timeStop;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("create_time")
    private Date createdTime;

    @JsonProperty("co2_savings")
    private double co2_savings;

    @JsonProperty("monetary_savings")
    private double financeSavings;

    @JsonProperty("intervals")
    private List<SchedulerInterval> intervals = new ArrayList<>();

    private GridDataAggregated moers;

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

    public Long getInitialEnergy() {
        return initialEnergy;
    }

    public void setInitialEnergy(Long initialEnergy) {
        this.initialEnergy = initialEnergy;
    }

    public SmScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(SmScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Double getEndSoc() {
        return endSoc;
    }

    public void setEndSoc(Double endSoc) {
        this.endSoc = endSoc;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getTotalEnergy() {
        return totalEnergy;
    }

    public void setTotalEnergy(Long totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public GridDataAggregated getMoers() {
        return moers;
    }

    public void setMoers(GridDataAggregated moers) {
        this.moers = moers;
    }
}
