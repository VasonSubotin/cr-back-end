package com.sm.client.model.eco;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ForecastData implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("generated_at")
    private Date generatedAt;


    @JsonProperty("forecast")
    private List<ForecastDetails> forecasts;

    public Date getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<ForecastDetails> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<ForecastDetails> forecasts) {
        this.forecasts = forecasts;
    }
}
