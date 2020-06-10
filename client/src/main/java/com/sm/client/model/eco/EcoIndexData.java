package com.sm.client.model.eco;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class EcoIndexData {
    @JsonProperty("ba")
    private String locationId;

    @JsonProperty("freq")
    private Long frequence;

    @JsonProperty("percent")
    private Integer percent;

    @JsonProperty("moer")
    private Long moer;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss'Z'") // , timezone = "UTC"
    @JsonProperty("point_time")
    private Date pointTime;

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Long getFrequence() {
        return frequence;
    }

    public void setFrequence(Long frequence) {
        this.frequence = frequence;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public Long getMoer() {
        return moer;
    }

    public void setMoer(Long moer) {
        this.moer = moer;
    }

    public Date getPointTime() {
        return pointTime;
    }

    public void setPointTime(Date pointTime) {
        this.pointTime = pointTime;
    }
}
