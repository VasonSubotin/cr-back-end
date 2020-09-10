package com.sm.client.utils.intervals;

import java.util.Date;

public class Interval<T> {
    private long start;
    private long stop;
    private T data;

    public Interval() {
    }

    public Interval(long start, long stop, T data) {
        this.start = start;
        this.stop = stop;
        this.data = data;
    }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
