package com.sm.model.cache;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CoordinatesCache")
public class Coordinates implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "V_ADDRESS")
    private String address;

    @Column(name = "F_LATITUDE")
    private double latitude;

    @Column(name = "F_LONGITUDE")
    private double longitude;

    public Coordinates() {
    }

    public Coordinates(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
