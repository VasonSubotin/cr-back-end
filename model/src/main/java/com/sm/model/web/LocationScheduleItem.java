package com.sm.model.web;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class LocationScheduleItem implements Serializable {
    private Date start;
    private Date stop;
    private List<LocationDistance> locationDistances;

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

    public List<LocationDistance> getLocationDistances() {
        return locationDistances;
    }

    public void setLocationDistances(List<LocationDistance> locationDistances) {
        this.locationDistances = locationDistances;
    }

    public static class LocationDistance implements Serializable {
        private Long locationId;
        private double price;
        private double distance;
        private double latitude;
        private double longitude;

        public Long getLocationId() {
            return locationId;
        }

        public void setLocationId(Long locationId) {
            this.locationId = locationId;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
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

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
