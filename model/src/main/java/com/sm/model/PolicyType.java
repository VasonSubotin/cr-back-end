package com.sm.model;

public enum PolicyType {
    ECO("This policy has priority to minimize CO2 impact"),
    ECO_PRICE("This policy has priority to minimize CO2 impact and lower price"),
    SIMPLE("This policy just have any priority"),
    PRICE("This policy should minimize price only");

    private String description;

    PolicyType(String description) {
        this.description = description;
    }

}
