package com.sm.model;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Locations")
public class SmLocation {


//    ID_LOCATION INTEGER Primary key AUTOINCREMENT,
//    V_NAME varchar(32),
//    V_DESCRIPTION varchar(128),
//    N_LATITUDE numeric(10),
//    N_LONGITUTE numeric(10),
//    V_TIME_ZONE varchar(8),
//    DT_CREATED datetime,
//    B_TOU_ENABLED numeric(1),
//    B_DELETED numeric(1)


    @Id
    @Column(name = "ID_LOCATION")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLocation;

    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "V_NAME")
    private String name;

    @Column(name = "V_DESCRIPTION")
    private String description;

    @Column(name = "N_LATITUDE")
    private Double latitude;

    @Column(name = "N_LONGITUDE")
    private Double longitude;

    @Column(name = "V_TIME_ZONE")
    private String timeZone;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_TOU_ENABLED")
    private Boolean touEnabled;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Long idLocation) {
        this.idLocation = idLocation;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Date getDtCreated() {
        return dtCreated;
    }

    public void setDtCreated(Date dtCreated) {
        this.dtCreated = dtCreated;
    }

    public Boolean getTouEnabled() {
        return touEnabled;
    }

    public void setTouEnabled(Boolean touEnabled) {
        this.touEnabled = touEnabled;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}