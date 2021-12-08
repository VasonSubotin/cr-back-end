package com.sm.client.services;

import com.sm.client.model.smartcar.VehicleData;
import net.iakovlev.timeshape.TimeZoneEngine;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.util.TimeZone;

@Service
//@Scope("singleton")
public class TimeZoneServiceImpl {

    private TimeZoneEngine engine;
    private boolean initialized = false;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            this.engine = TimeZoneEngine.initialize();
            this.initialized = true;
        }).start();
    }

    public TimeZone getTimeZone(VehicleData smData) {

        return !initialized || smData == null || smData.getLocation() == null || smData.getLocation().getData() == null
                ? TimeZone.getDefault()
                : TimeZone.getTimeZone(engine.query(smData.getLocation().getData().getLatitude(), smData.getLocation().getData().getLongitude()).orElseGet(ZoneId::systemDefault));
    }

    public TimeZone getTimeZone(Double latitude, Double longitude) {

        return latitude == null || longitude == null || !initialized
                ? TimeZone.getDefault()
                : TimeZone.getTimeZone(engine.query(latitude, longitude).orElseGet(ZoneId::systemDefault));
    }


}
