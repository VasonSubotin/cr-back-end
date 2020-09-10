package com.sm.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DREvents")
public class SmDREvent {

    @Id
    @Column(name = "ID_DR_EVENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDrEvent;

    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "N_START")
    private Long start; // time im mins from the beginning of day

    @Column(name = "N_STOP")
    private Long stop; // time im mins from the beginning of day

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdDrEvent() {
        return idDrEvent;
    }

    public void setIdDrEvent(Long idDrEvent) {
        this.idDrEvent = idDrEvent;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
