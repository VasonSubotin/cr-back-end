package com.sm.client.model.smartcar;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class LocationPoint implements Serializable {

    @JsonProperty("id")
    private Long locationId;

    @JsonProperty("address")
    private String address;

    @JsonProperty("name")
    private String name;// can be description

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("latitude")
    private double latitude;

    public LocationPoint() {
    }

    public LocationPoint(Long locationId, String address, String name, double longitude, double latitude) {
        this.locationId = locationId;
        this.address = address;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
