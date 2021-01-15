package com.sm.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Schedules")
public class SmSchedules {

    @Id
    @Column(name = "ID_SCHEDULE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSchedule;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    @Column(name = "F_CARBON_IMPACT")
    private Double carbonImpact;

    @Column(name = "F_CARBON_SAVINGS")
    private Double carbonSavings;

    @Column(name = "N_INIT_ENERGY")
    private Long initEnergy;

    @Column(name = "F_END_SOC")
    private Double endSoc;

    @Column(name = "F_FINANCE_SAVINGS")
    private Double financeSavings;

    @Column(name = "SESSION_ID")
    private Long sessionId;

    @Column(name = "N_CAPACITY")
    private Long capacity;

    @Column(name = "V_SCHEDULE_TYPE")
    private SmScheduleType scheduleType;

    @Column(name = "POLICY_ID")
    private Long policyId;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "DT_START")
    private Date dtStart;

    @Column(name = "DT_STOP")
    private Date dtStop;

    @Column(name = "BB_DATA")
    private byte[] data;

    public SmSchedules() {
    }

    public SmSchedules(Long idSchedule, Long accountId, Long locationId, Long resourceId, Double carbonImpact, Double carbonSavings, Long initEnergy, Double financeSavings, Long sessionId, Long policyId, Date dtCreated, Date dtStart, Date dtStop, SmScheduleType smScheduleType, Double endSoc, Long capacity) {
        this.idSchedule = idSchedule;
        this.accountId = accountId;
        this.locationId = locationId;
        this.resourceId = resourceId;
        this.carbonImpact = carbonImpact;
        this.carbonSavings = carbonSavings;
        this.initEnergy = initEnergy;
        this.financeSavings = financeSavings;
        this.sessionId = sessionId;
        this.policyId = policyId;
        this.dtCreated = dtCreated;
        this.dtStart = dtStart;
        this.dtStop = dtStop;
        this.scheduleType = smScheduleType;
        this.capacity = capacity;
        this.endSoc = endSoc;
    }

    public Long getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(Long idSchedule) {
        this.idSchedule = idSchedule;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Double getCarbonImpact() {
        return carbonImpact;
    }

    public void setCarbonImpact(Double carbonImpact) {
        this.carbonImpact = carbonImpact;
    }

    public Double getCarbonSavings() {
        return carbonSavings;
    }

    public void setCarbonSavings(Double carbonSavings) {
        this.carbonSavings = carbonSavings;
    }

    public Double getFinanceSavings() {
        return financeSavings;
    }

    public void setFinanceSavings(Double financeSavings) {
        this.financeSavings = financeSavings;
    }

    public Long getInitEnergy() {
        return initEnergy;
    }

    public void setInitEnergy(Long initEnergy) {
        this.initEnergy = initEnergy;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public Date getDtStart() {
        return dtStart;
    }

    public void setDtStart(Date dtStart) {
        this.dtStart = dtStart;
    }

    public Date getDtStop() {
        return dtStop;
    }

    public void setDtStop(Date dtStop) {
        this.dtStop = dtStop;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
}
