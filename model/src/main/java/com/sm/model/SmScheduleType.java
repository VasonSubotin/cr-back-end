package com.sm.model;


public enum SmScheduleType {
    DRV("Driving to the location"),
    FLG("Flying to the location"),
    NCHR("Connected to charge but not charging"),
    CHR("Charging");


    private String description;

    SmScheduleType(String description) {
        this.description = description;
    }
}

