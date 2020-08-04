package com.sm.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DREvents")
public class SmDREvent {

    @Id
    @Column(name = "ID_DR_EVENT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTou;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "N_START")
    private Long start;

    @Column(name = "N_STOP")
    private Long stop;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdTou() {
        return idTou;
    }

    public void setIdTou(Long idTou) {
        this.idTou = idTou;
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
