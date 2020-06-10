package com.sm.client.model.to;

import java.io.Serializable;
import java.util.Date;

public class EventIntervalTO implements Serializable {
    private Date start;
    private Date stop;
    private long duration;

    public EventIntervalTO() {
    }

    public EventIntervalTO(EventInterval eventInterval) {
        this.start = new Date(eventInterval.getStart());
        this.stop = new Date(eventInterval.getStop());
        this.duration = eventInterval.getDuration();
    }

    public EventIntervalTO(long start, long stop, long duration) {
        this.start = new Date(start);
        this.stop = new Date(stop);
        this.duration = duration;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
