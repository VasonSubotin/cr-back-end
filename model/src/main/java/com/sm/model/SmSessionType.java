package com.sm.model;

import javax.persistence.*;

@Entity
@Table(name = "SessionTypes")
public class SmSessionType {

    @Id
    @Column(name = "ID_SESSION_TYPE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLocation;

    @Column(name = "V_NAME")
    private String name;

    @Column(name = "V_DESCRIPTION")
    private String description;

    public Long getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Long idLocation) {
        this.idLocation = idLocation;
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
}
