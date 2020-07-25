package com.sm.client.services;

import com.sm.dao.cache.CoordinatesCacheDao;
import com.sm.model.cache.Coordinates;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GoogleLocationService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleLocationService.class);

    @Autowired
    private CoordinatesCacheDao coordinatesCacheDao;

    @Autowired
    private RestTemplate googleLocationTemplate;

    @Value("${rt.googleLocation.apikey:#{null}}")
    private String apiKey;

    @Value("${rt.googleLocation.url:https://maps.googleapis.com/maps/api/geocode/json?}")
    private String url;

    public Coordinates getLatitudeAndLongitute(String address) {
        if (address == null) {
            return null;
        }
        //trying to look at cache
        String key = address.trim().toUpperCase();
        Coordinates coordinates = coordinatesCacheDao.loadCoordinates(key);

        if (coordinates != null) {
            return coordinates;
        }

        ResponseEntity<Map> responseEntity = googleLocationTemplate.getForEntity(url + "key=" + apiKey + "&address=" + address, Map.class);
        try {
            List<Map<String, Object>> results = (List<Map<String, Object>>) responseEntity.getBody().get("results");
            if (results != null) {
                for (Map<String, Object> result : results) {
                    double[] ret = getFromResult(result);
                    if (ret != null && ret.length == 2) {
                        coordinates = new Coordinates(key, ret[0], ret[1]);
                        coordinatesCacheDao.saveCoordinates(coordinates);
                        return coordinates;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    private double[] getFromResult(Map<String, Object> result) {
        Map<String, Object> location = (Map<String, Object>) ((Map<String, Object>) result.get("geometry")).get("location");

        Double lat = getDouble(location, "lat");
        if (lat == null) {
            return null;
        }
        Double lng = getDouble(location, "lng");
        if (lng == null) {
            return null;
        }
        return new double[]{lat, lng};
    }

    private Double getDouble(Map<String, Object> map, String fieldName) {
        if (map != null) {
            return (Double) map.get(fieldName);
        }
        return null;
    }
}
