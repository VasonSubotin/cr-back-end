package com.sm.client.services;

import com.sm.client.model.eco.EcoIndexData;
import com.sm.client.model.eco.ForecastData;
import com.sm.client.model.eco.GridData;
import com.sm.client.model.eco.LocationData;
import com.sm.client.model.to.EventInterval;
import com.sm.model.SmException;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

public interface EcoService {
    List<ForecastData> getEcoForecast(
            String locationId,
            String starttime,
            String endtime) throws Exception;

    List<GridData> getEcoData(String obrev,
                              Double latitude,
                              Double longitude,
                              Date startTime,
                              Date endTime,
                              String moerversion,
                              String style) throws Exception;

    List<EventInterval> getEventInterval(Long resourceId) throws SmException;

    List<EventInterval> getEventIntervalMock(Date start, Date stop);

    List<GridData> getEcoDataMock(String obrev,
                                  Double latitude,
                                  Double longitude,
                                  Date startTime,
                                  Date endTime,
                                  String moerversion,
                                  String style) throws Exception;


    LocationData getLocation(double latitude, double longitude) throws MalformedURLException;

    EcoIndexData getEcoInex(
            String obrev,
            Double latitude,
            Double longitude,
            String style) throws Exception;
}
