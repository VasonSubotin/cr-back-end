package com.sm.client.services;

import com.sm.client.model.eco.EcoIndexData;
import com.sm.client.model.eco.ForecastData;
import com.sm.client.model.eco.GridData;
import com.sm.client.model.eco.LocationData;
import com.sm.client.model.to.EventInterval;
import com.sm.client.utils.StringDateUtil;
import com.sm.model.Constants;
import com.sm.model.SmDREvent;
import com.sm.model.SmException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sm.client.utils.StringDateUtil.getDayOfWeek;

@Service
public class EcoServiceImpl implements EcoService {

    private Logger logger = LoggerFactory.getLogger(EcoServiceImpl.class);

    @Value("${rt.eco.forecast.url:https://api2.watttime.org/v2/forecast/}")
    private String urlEcoForecast;

    @Value("${rt.eco.location.url:https://api2.watttime.org/v2/ba-from-loc/}")
    private String urlEcoLocation;

    @Value("${rt.eco.index.url:https://api2.watttime.org/v2/index/}")
    private String urlEcoIndex;

    @Value("${rt.eco.data.url:https://api2.watttime.org/v2/data/}")
    private String urlEcoData;

    @Value("${rt.eco.username:#{null}}")
    private String username;

    @Value("${rt.eco.password:#{null}}")
    private String password;

    private String currentToken;

    @Autowired
    private RestTemplate ecoTemplate;

    @Autowired
    private DREventService drEventService;

    private List<GridData> mockCache = new ArrayList<>();

    private Map<Integer, List<EventInterval>> mockEventCache = new HashMap<>();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");

    static {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @PostConstruct
    public void init() throws Exception {
//        BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/moc_eco.txt")));
//        while (in.ready()) {
//            String line = in.readLine();
//            String s[] = line.split(" ");
//            GridData gridData = new GridData();
//            mockCache.add(gridData);
//            gridData.setPointTime(sdf.parse(s[0]));
//            gridData.setDatatype("MOER");
//            gridData.setFrequence(300L);
//            gridData.setValue(Double.valueOf(s[1]));
//            gridData.setVersion("MockData-2018-11");
//        }
//        in.close();

//        in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/moc_event.txt")));
//        while (in.ready()) {
//            String line = in.readLine();
//            String s[] = line.split(" ");
//            EventInterval eventInterval = new EventInterval();
//            Date startDate = sdf.parse(s[0]);
//            eventInterval.setStart(startDate.getTime());
//            Integer dayOfWeek = getDayOfWeek(startDate);
//            List<EventInterval> events = mockEventCache.get(dayOfWeek);
//            if (events == null) {
//                events = new ArrayList<>();
//                mockEventCache.put(dayOfWeek, events);
//            }
//            eventInterval.setDuration(Long.parseLong(s[1]));
//            eventInterval.setStop(eventInterval.getStart() + eventInterval.getDuration());
//            events.add(eventInterval);
//        }
//        in.close();
    }


    @Override
    public List<ForecastData> getEcoForecast(
            String locationId,
            String starttime,
            String endtime) throws Exception {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlEcoForecast)
                .queryParam("ba", locationId);
        if (starttime != null) {
            builder.queryParam("starttime", starttime);
        }
        if (endtime != null) {
            builder.queryParam("endtime", endtime);
        }
        int tries = 3;
        while (tries > 0) {
            try {
                tries--;
                ResponseEntity<List> result = ecoTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(buildBearerHttpHeader(currentToken)), List.class);
                return result.getBody();
            } catch (org.springframework.web.client.HttpClientErrorException.Forbidden ex) {
                getToken();
            }
        }
        return null;
    }

    @Override
    public List<GridData> getEcoData(String obrev,
                                     Double latitude,
                                     Double longitude,
                                     Date startTime,
                                     Date endTime,
                                     String moerversion,
                                     String style) throws Exception {
        try {
            return getEcoDataInternal(obrev, latitude, longitude, startTime, endTime, moerversion, style);
        } catch (Exception ex) {
            logger.error("Failed to get location eco time watt by params obrev={}, latitude={}, longitude={} -- wil try again with default location CAISO_ZP26", obrev, latitude, longitude);
            return getEcoDataInternal("CAISO_ZP26", latitude, longitude, startTime, endTime, moerversion, style);
        }
    }

    private List<GridData> getEcoDataInternal(String obrev,
                                              Double latitude,
                                              Double longitude,
                                              Date startTime,
                                              Date endTime,
                                              String moerversion,
                                              String style) throws Exception {

        if ((latitude == null || longitude == null) && (obrev == null || obrev.isEmpty())) {
            throw new Exception("Both ba and  latitude/longitude are empty. You need to setup at least one of them");
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlEcoData);
        if (obrev != null && !obrev.isEmpty()) {
            builder.queryParam("ba", obrev);
        } else {
            builder.queryParam("latitude", latitude).queryParam("longitude", longitude);
        }
        builder.queryParam("style", style);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (startTime != null) {
            // we will do request for the 7 days back
            Date start = new Date(startTime.getTime() - 7 * Constants.DAYS_LENGTH_IN_MILLS);
            builder.queryParam("starttime", sdf.format(start));
        }
        if (endTime != null) {
            // we will do request for the 7 days back
            Date stop = new Date(endTime.getTime() - 7 * Constants.DAYS_LENGTH_IN_MILLS);
            builder.queryParam("endtime", sdf.format(stop));
        }
        if (moerversion != null) {
            builder.queryParam("moerversion", moerversion);
        }

        int tries = 3;
        Exception last = null;
        while (tries > 0) {
            try {
                tries--;
                ResponseEntity<GridData[]> result = ecoTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(buildBearerHttpHeader(currentToken)), GridData[].class);
                List<GridData> records = Arrays.asList(result.getBody());
                splitByGrid(records, 7 * Constants.DAYS_LENGTH_IN_MILLS);
                return records;
            } catch (org.springframework.web.client.HttpClientErrorException.Forbidden ex) {
                getToken();
                last = ex;
            }
        }
        if (last != null) {
            throw last;
        }
        return null;
    }

    @Override
    public List<EventInterval> getEventInterval(Long resourceId) throws SmException {
        List<SmDREvent> result = drEventService.getDREventsByResourceId(resourceId);

        List<EventInterval> ret = new ArrayList<>();
        for (SmDREvent smDREvent : result) {
            EventInterval eventInterval = new EventInterval();
            eventInterval.setStart(StringDateUtil.setTimeFromMinutesOfDay(new Date(), smDREvent.getStart()).getTime());
            eventInterval.setStop(StringDateUtil.setTimeFromMinutesOfDay(new Date(), smDREvent.getStop()).getTime());
            eventInterval.setDuration(eventInterval.getStop() - eventInterval.getStart());
            ret.add(eventInterval);
        }
        return ret;
    }


    @Override
    public List<EventInterval> getEventIntervalMock(Date start, Date stop) {

        List<EventInterval> ret = new ArrayList<>();
        long startTime = start.getTime();
        while (startTime < stop.getTime()) {
            List<EventInterval> retA = mockEventCache.get(getDayOfWeek(new Date(startTime)));
            if (retA != null) {
                //fixing date
                for (EventInterval r : retA) {
                    EventInterval eventInterval = new EventInterval();
                    eventInterval.setStart(StringDateUtil.setDateFrom(new Date(startTime), new Date(r.getStart())).getTime());
                    eventInterval.setStop(StringDateUtil.setDateFrom(new Date(startTime), new Date(r.getStop())).getTime());
                    eventInterval.setDuration(r.getDuration());
                    ret.add(eventInterval);
                }
            }
            startTime += (Constants.DAYS_LENGTH_IN_MILLS);
        }

        return ret;
    }

    @Override
    public List<GridData> getEcoDataMock(
            String obrev,
            Double latitude,
            Double longitude,
            Date startTime,
            Date endTime,
            String moerversion,
            String style) throws Exception {


        splitByGrid(mockCache, 0);
        //ranging by interval

        List<GridData> ret = new ArrayList<>();
        for (GridData gd : mockCache) {
            if (startTime.before(gd.getPointTime()) && endTime.after(gd.getPointTime())) {
                ret.add(gd);
            }
        }
        return ret;
    }

    private void splitByGrid(List<GridData> dataRecords, long shift) {
        dataRecords.sort(Comparator.comparing(GridData::getPointTime));
        GridData lastGridData = null;
        for (GridData gridData : dataRecords) {
            gridData.setPointTime(new Date(gridData.getPointTime().getTime() + shift));
            gridData.setStart(gridData.getPointTime().getTime());
            if (gridData.getFrequence() != null) {
                gridData.setStop(gridData.getPointTime().getTime() + gridData.getFrequence() * 1000);
                lastGridData = gridData;
                continue;
            }
            if (lastGridData != null) {
                lastGridData.setStop(gridData.getPointTime().getTime());
            }
            lastGridData = gridData;
        }
        lastGridData.setStop(lastGridData.getPointTime().getTime() + lastGridData.getFrequence() * 1000);
    }


    @Override
    public LocationData getLocation(double latitude, double longitude) throws MalformedURLException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlEcoLocation)
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude);
        int tries = 3;
        while (tries > 0) {
            try {
                tries--;
                ResponseEntity<LocationData> result = ecoTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(buildBearerHttpHeader(currentToken)), LocationData.class);
                return result.getBody();
            } catch (org.springframework.web.client.HttpClientErrorException.Forbidden ex) {
                getToken();
            }
        }
        return null;
    }

    @Override
    public EcoIndexData getEcoInex(
            String obrev,
            Double latitude,
            Double longitude,
            String style) throws Exception {

        if ((latitude == null || longitude == null) && (obrev == null || obrev.isEmpty())) {
            throw new Exception("Both ba and  latitude/longitude are empty. You need to setup at least one of them");
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlEcoIndex);
        if (obrev != null && !obrev.isEmpty()) {
            builder.queryParam("ba", obrev);
        } else {
            builder.queryParam("latitude", latitude).queryParam("longitude", longitude);
        }
        builder.queryParam("style", style);
        int tries = 3;
        while (tries > 0) {
            try {
                tries--;
                ResponseEntity<EcoIndexData> result = ecoTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(buildBearerHttpHeader(currentToken)), EcoIndexData.class);
                return result.getBody();
            } catch (org.springframework.web.client.HttpClientErrorException.Forbidden ex) {
                getToken();
            }
        }
        return null;
    }


    private void getToken() {
        String old = currentToken;
        synchronized (this) {
            if (currentToken != old) {
                return;
            }
            logger.info("-- Requesting token --");
            ResponseEntity<Map> result = ecoTemplate.exchange("https://api2.watttime.org/v2/login/", HttpMethod.GET, new HttpEntity<>(buildAuthHttpHeader()), Map.class);
            this.currentToken = (String) result.getBody().get("token");
        }
    }

    private HttpHeaders buildBearerHttpHeader(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        return httpHeaders;
    }

    private HttpHeaders buildAuthHttpHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Basic " + new String(Base64.encodeBase64((username + ":" + password).getBytes(Charset.forName("US-ASCII")))));
        return httpHeaders;
    }

    public void setDrEventService(DREventService drEventService) {
        this.drEventService = drEventService;
    }

    public void setEcoTemplate(RestTemplate ecoTemplate) {
        this.ecoTemplate = ecoTemplate;
    }

    public void setUrlEcoForecast(String urlEcoForecast) {
        this.urlEcoForecast = urlEcoForecast;
    }

    public void setUrlEcoLocation(String urlEcoLocation) {
        this.urlEcoLocation = urlEcoLocation;
    }

    public void setUrlEcoIndex(String urlEcoIndex) {
        this.urlEcoIndex = urlEcoIndex;
    }

    public void setUrlEcoData(String urlEcoData) {
        this.urlEcoData = urlEcoData;
    }
}
