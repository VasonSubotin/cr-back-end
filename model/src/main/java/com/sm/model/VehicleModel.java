package com.sm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Vehicles")
public class VehicleModel implements Serializable {

    @Id
    @Column(name = "ID_VEHICLE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idUserSession;

    @Column(name = "V_MAKER")
    private String maker;

    @Column(name = "V_MODEL")
    private String model;

    @Column(name = "N_YEAR")
    private int year;

    @Column(name = "N_BATERY")
    private long battery;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_DELETED")
    private boolean deleted;

    public long getIdUserSession() {
        return idUserSession;
    }

    public void setIdUserSession(long idUserSession) {
        this.idUserSession = idUserSession;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getBattery() {
        return battery;
    }

    public void setBattery(long battery) {
        this.battery = battery;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
