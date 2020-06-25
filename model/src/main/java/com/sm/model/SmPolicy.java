package com.sm.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "Policies")
public class SmPolicy implements Serializable {

    @Id
    @Column(name = "ID_POLICY")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPolicy;

    @Column(name = "V_NAME")
    private String name;

    @Column(name = "V_DESCRIPTION")
    private String description;

    @Column(name = "DT_CREATED")
    private Date dtCreated;

    @Column(name = "B_DELETED")
    private Boolean deleted;

    public Long getIdPolicy() {
        return idPolicy;
    }

    public void setIdPolicy(Long idPolicy) {
        this.idPolicy = idPolicy;
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
