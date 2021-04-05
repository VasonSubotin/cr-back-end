package com.sm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "TimeOfUsages")
public class SmTimeOfUsage implements Serializable {

    @Id
    @Column(name = "ID_TOU")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTou;

    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "N_START")
    private Long start; // in minutes

    @Column(name = "N_STOP")
    private Long stop; // in minutes

    @Column(name = "V_TZ_INDEX")
    private String timeZoneIndex;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_ACTIVE")
    private Boolean active;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdTou() {
        return idTou;
    }

    public void setIdTou(Long idTou) {
        this.idTou = idTou;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getStop() {
        return stop;
    }

    public void setStop(Long stop) {
        this.stop = stop;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public String getTimeZoneIndex() {
        return timeZoneIndex;
    }

    public void setTimeZoneIndex(String timeZoneIndex) {
        this.timeZoneIndex = timeZoneIndex;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
}
