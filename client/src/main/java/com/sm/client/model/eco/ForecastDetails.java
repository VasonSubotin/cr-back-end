package com.sm.client.model.eco;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class ForecastDetails implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'" , timezone = "UTC")
    @JsonProperty("point_time")
    private Date pointTime;

    @JsonProperty("ba")
    private String locationId;

    @JsonProperty("value")
    private long value;

    @JsonProperty("version")
    private String version;

    public Date getPointTime() {
        return pointTime;
    }

    public void setPointTime(Date pointTime) {
        this.pointTime = pointTime;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}