package com.sm.ocpp.to;

import java.util.LinkedList;
import java.util.UUID;

public class OCPPScheduleData {
    private UUID uuid;
    private Long resourceId;
    private String extrenalId;
    private int connectors;
    private LinkedList<OCPPScheduleEvent> events = new LinkedList<>();

    public OCPPScheduleData(UUID uuid, Long resourceId, String extrenalId, int connectors) {
        this.uuid = uuid;
        this.resourceId = resourceId;
        this.extrenalId = extrenalId;
        this.connectors = connectors;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getExtrenalId() {
        return extrenalId;
    }

    public void setExtrenalId(String extrenalId) {
        this.extrenalId = extrenalId;
    }

    public LinkedList<OCPPScheduleEvent> getEvents() {
        return events;
    }

    public void setEvents(LinkedList<OCPPScheduleEvent> events) {
        this.events = events;
    }

    public int getConnectors() {
        return connectors;
    }

    public void setConnectors(int connectors) {
        this.connectors = connectors;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
