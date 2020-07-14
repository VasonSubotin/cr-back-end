package com.sm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Sessions")
public class SmSession implements Serializable {

    @Id
    @Column(name = "ID_SESSION")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSession;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "N_LATITUDE")
    private Double latitude;

    @Column(name = "N_LONGITUDE")
    private Double longitude;

    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    @Column(name = "DT_UPDATED")
    private Date dtUpdated;

    @Column(name = "DT_START")
    private Date dtStart;

    @Column(name = "DT_STOP")
    private Date dtStop;

    @Column(name = "N_DURATION")
    private Long duration;

    @Column(name = "N_ENERGY")
    private Long energy;

    @Column(name = "F_CARBON_IMPACT")
    private Double carbonImpact;

    @Column(name = "F_CARBON_SAVINGS")
    private Double carbonSavings;

    @Column(name = "F_FINANCE_SAVINGS")
    private Double financeSavings;

    @Column(name = "SESSION_TYPE_ID")
    private Long sessionTypeId;

    @Column(name = "V_STATUS")
    private String status;

    @Column(name = "B_CLOSED")
    private Boolean closed = false;

    public Long getIdSession() {
        return idSession;
    }

    public void setIdSession(Long idSession) {
        this.idSession = idSession;
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

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Date getDtUpdated() {
        return dtUpdated;
    }

    public void setDtUpdated(Date dtUpdated) {
        this.dtUpdated = dtUpdated;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getEnergy() {
        return energy;
    }

    public void setEnergy(Long energy) {
        this.energy = energy;
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

    public Long getSessionTypeId() {
        return sessionTypeId;
    }

    public void setSessionTypeId(Long sessionTypeId) {
        this.sessionTypeId = sessionTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }
}
