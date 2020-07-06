package com.sm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Events")
public class SmEvent implements Serializable {

    @Id
    @Column(name = "ID_EVENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvent;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    @Column(name = "SESSION_ID")
    private Long sessionId;

    @Column(name = "IEVENT_TYPE_ID")
    private Long sessionTypeId;

    @Column(name = "N_LATITUDE")
    private Double latitude;

    @Column(name = "N_LONGITUDE")
    private Double longitude;

    @Column(name = "N_CURRENT")
    private Long current;

    @Column(name = "N_ENERGY")
    private Long energy;

    @Column(name = "N_POWER")
    private Long power;

    @Column(name = "F_EXT_TEMPERATURE")
    private Double extTemperature;

    @Column(name = "F_INT_TEMPERATURE")
    private Double intTemperature;

    @Column(name = "F_CARBON_IMPACT")
    private Double carbonImpact;

    @Column(name = "F_FINANCE_SAVINGS")
    private Double financeSavings;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    public Long getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(Long idEvent) {
        this.idEvent = idEvent;
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

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSessionTypeId() {
        return sessionTypeId;
    }

    public void setSessionTypeId(Long sessionTypeId) {
        this.sessionTypeId = sessionTypeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getEnergy() {
        return energy;
    }

    public void setEnergy(Long energy) {
        this.energy = energy;
    }

    public Long getPower() {
        return power;
    }

    public void setPower(Long power) {
        this.power = power;
    }

    public Double getExtTemperature() {
        return extTemperature;
    }

    public void setExtTemperature(Double extTemperature) {
        this.extTemperature = extTemperature;
    }

    public Double getIntTemperature() {
        return intTemperature;
    }

    public void setIntTemperature(Double intTemperature) {
        this.intTemperature = intTemperature;
    }

    public Double getCarbonImpact() {
        return carbonImpact;
    }

    public void setCarbonImpact(Double carbonImpact) {
        this.carbonImpact = carbonImpact;
    }

    public Double getFinanceSavings() {
        return financeSavings;
    }

    public void setFinanceSavings(Double financeSavings) {
        this.financeSavings = financeSavings;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }
}
