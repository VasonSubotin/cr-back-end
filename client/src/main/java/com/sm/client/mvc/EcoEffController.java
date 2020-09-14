package com.sm.client.mvc;

import com.sm.client.model.eco.EcoIndexData;
import com.sm.client.model.eco.ForecastData;
import com.sm.client.model.eco.GridData;
import com.sm.client.model.eco.LocationData;
import com.sm.client.services.EcoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

@RestController
public class EcoEffController {
    private Logger logger = LoggerFactory.getLogger(BuLogicController.class);

    @Autowired
    private EcoService ecoService;

    @RequestMapping(value = "/forecast", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ForecastData> getEcoForecast(
            @RequestParam(name = "ba") String locationId,
            @RequestParam(name = "starttime", required = false) String starttime,
            @RequestParam(name = "endtime", required = false) String endtime) throws Exception {

        return ecoService.getEcoForecast(locationId, starttime, endtime);
    }


    @RequestMapping(value = "/location", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public LocationData getLocation(
            @RequestParam(name = "latitude") double latitude,
            @RequestParam(name = "longitude") double longitude) throws MalformedURLException {

        return ecoService.getLocation(latitude, longitude);
    }


    @RequestMapping(value = "/ecoIndex", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public EcoIndexData getEcoInex(
            @RequestParam(name = "ba", required = false) String obrev,
            @RequestParam(name = "latitude", required = false) Double latitude,
            @RequestParam(name = "longitude", required = false) Double longitude,
            @RequestParam(name = "style", required = false, defaultValue = "all") String style) throws Exception {

        return ecoService.getEcoInex(obrev, latitude, longitude, style);
    }


    @RequestMapping(value = "/ecoData", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<GridData> getEcoData(
            @RequestParam(name = "ba", required = false) String obrev,
            @RequestParam(name = "latitude", required = false) Double latitude,
            @RequestParam(name = "longitude", required = false) Double longitude,
            @RequestParam(name = "starttime", required = false) Date starttime,
            @RequestParam(name = "endtime", required = false) Date endtime,
            @RequestParam(name = "moerversion", required = false) String moerversion,
            @RequestParam(name = "style", required = false, defaultValue = "all") String style) throws Exception {

        return ecoService.getEcoData(obrev, latitude, longitude, starttime, endtime, moerversion, style);
    }


}
