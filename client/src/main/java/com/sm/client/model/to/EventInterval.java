package com.sm.client.model.to;

import java.io.Serializable;

public class EventInterval implements Serializable {
    private long start;
    private long stop;
    private long duration;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
