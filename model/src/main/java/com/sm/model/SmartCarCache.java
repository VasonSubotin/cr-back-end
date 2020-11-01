package com.sm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "SmartCarCaches")
public class SmartCarCache {

    @Id
    @Column(name = "V_EXTERNAL_RESOURCE_ID")
    private String externalResourceId;

    @Column(name = "N_TIMING")
    private Long timing;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "BB_DATA")
    private byte[] data;

    public String getExternalResourceId() {
        return externalResourceId;
    }

    public void setExternalResourceId(String externalResourceId) {
        this.externalResourceId = externalResourceId;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Long getTiming() {
        return timing;
    }

    public void setTiming(Long timing) {
        this.timing = timing;
    }
}
