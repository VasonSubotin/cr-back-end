package com.sm.model;

public enum PolicyType {
    ECO(1L, "This policy has priority to minimize CO2 impact"),
    ECO_PRICE(2L, "This policy has priority to minimize CO2 impact and lower price"),
    SIMPLE(3L, "This policy just have any priority"),
    PRICE(4L, "This policy should minimize price only");

    private long id;
    private String description;

    PolicyType(long id, String description) {
        this.id = id;
        this.description = description;
    }

    public static final PolicyType getById(long id) {
        for (PolicyType p : values()) {
            if (p.id == id) {
                return p;
            }
        }
        return null;
    }

}
