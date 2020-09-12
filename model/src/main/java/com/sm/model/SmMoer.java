package com.sm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "MarginalOperatingEmissionsRate")
public class SmMoer implements Serializable {

    @Id
    @Column(name = "ID_MOER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMoer;

    @Column(name = "V_EXTERNAL_LOCATION_ID")
    private String externalLocationId;

    @Column(name = "DT_START")
    private Date start;

    @Column(name = "DT_STOP")
    private Date stop;

    @Column(name = "DT_CREATED")
    private Date name;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdMoer() {
        return idMoer;
    }

    public void setIdMoer(Long idMoer) {
        this.idMoer = idMoer;
    }

    public String getExternalLocationId() {
        return externalLocationId;
    }

    public void setExternalLocationId(String externalLocationId) {
        this.externalLocationId = externalLocationId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    public Date getName() {
        return name;
    }

    public void setName(Date name) {
        this.name = name;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
