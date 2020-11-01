package com.sm.client.model.eco;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class GridData implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonProperty("point_time")
    private Date pointTime;

    private transient long start;
    private transient long stop;

    @JsonProperty("datatype")
    private String datatype;

    @JsonProperty("frequency")
    private Long frequence;

    @JsonProperty("market")
    private String market;

    @JsonProperty("ba")
    private String locationId;

    @JsonProperty("value")
    private Double value;

    @JsonProperty("version")
    private String version;

    public Date getPointTime() {
        return pointTime;
    }

    public void setPointTime(Date pointTime) {
        this.pointTime = pointTime;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public Long getFrequence() {
        return frequence;
    }

    public void setFrequence(Long frequence) {
        this.frequence = frequence;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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
}
