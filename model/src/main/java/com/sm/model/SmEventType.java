package com.sm.model;

import javax.persistence.*;

@Entity
@Table(name = "EventTypes")
public class SmEventType {

    @Id
    @Column(name = "ID_EVENT_TYPE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventType;

    @Column(name = "V_NAME")
    private String name;

    @Column(name = "V_DESCRIPTION")
    private String description;

    public Long getEventType() {
        return eventType;
    }

    public void setEventType(Long eventType) {
        this.eventType = eventType;
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
