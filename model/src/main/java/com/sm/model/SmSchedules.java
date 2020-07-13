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

    @Column(name = "F_FINANCE_SAVINGS")
    private Double financeSavings;

    @Column(name = "SESSION_ID")
    private Long sessionId;

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
}
