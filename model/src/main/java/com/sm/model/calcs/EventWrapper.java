package com.sm.model.calcs;

import com.sm.model.cache.Coordinates;

import java.util.Date;

public class EventWrapper {
    private Date start;
    private Date stop;
    private Coordinates coordinates;
    private Double distanceToTheNextEventLocation;
    private String name;

    public EventWrapper(Date start, Date stop, Coordinates coordinates, String name) {
        this.start = start;
        this.stop = stop;
        this.coordinates = coordinates;
        this.name = name;
    }

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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDistanceToTheNextEventLocation() {
        return distanceToTheNextEventLocation;
    }

    public void setDistanceToTheNextEventLocation(Double distanceToTheNextEventLocation) {
        this.distanceToTheNextEventLocation = distanceToTheNextEventLocation;
    }
}
