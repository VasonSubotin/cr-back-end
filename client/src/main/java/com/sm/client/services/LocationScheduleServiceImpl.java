package com.sm.client.services;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.sm.client.utils.GeoUtils;
import com.sm.dao.LocationDao;
import com.sm.model.SmException;
import com.sm.model.SmLocation;
import com.sm.model.cache.Coordinates;
import com.sm.model.web.LocationScheduleItem;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class LocationScheduleServiceImpl implements LocationScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(LocationScheduleServiceImpl.class);
    private int maxResult = 100;


    @Autowired
    private GoogleService googleService;

    @Autowired
    private GoogleLocationService googleLocationService;

    @Autowired
    private LocationDao locationDao;


    @Override
    public List<LocationScheduleItem> calculate(Long accountId, Long resourceId, double maxRadius, Date start, Date stop) throws SmException, IOException {
        Events events = googleService.getCalendar().events().list("primary").setMaxResults(maxResult).execute();
        if (events == null || events.isEmpty()) {
            // no events found
            throw new SmException("No calendar events found !", HttpStatus.SC_NOT_FOUND);
        }

        List<EventWrapper> eventsWrappers = new ArrayList<>();
        for (Event event : events.getItems()) {
            if (event.getLocation() != null && (event.getLocation().toUpperCase().startsWith("HTTP://") || event.getLocation().toUpperCase().startsWith("HTTPS://"))) {
                //can't detect address for this location
                logger.debug("-- Location {} does not look like address - it will be ignored --", event.getLocation());
                continue;
            }
            Date stopDate = makeDate(event.getEnd());
            Date startDate = makeDate(event.getStart());

            if (stopDate.before(start) || startDate.after(stop)) {
                //no cross - excluding
                logger.debug("Event with date interval [{} - {}] does not cross with out range [{} - {}] - ignoring", startDate, stopDate, start, stop);
                continue;
            }
            //Looking for location coordinates
            Coordinates coordinates = googleLocationService.getLatitudeAndLongitute(event.getLocation());
            if (coordinates == null) {
                logger.debug("-- Failed to find address location {} - it will be ignored --", event.getLocation());
                continue;
            }
            eventsWrappers.add(new EventWrapper(startDate, stopDate, coordinates, event.getDescription()));
        }

        List<LocationScheduleItem> ret = new ArrayList<>();
        //now looking for cheapest location in 500м радиус
        for (EventWrapper eventsWrapper : eventsWrappers) {
            double latitudes[] = GeoUtils.calculateLatRange(eventsWrapper.getCoordinates().getLatitude(), 1000);
            double longitude[] = GeoUtils.calculateLngRange(eventsWrapper.getCoordinates().getLatitude(), eventsWrapper.getCoordinates().getLongitude(), 1000);
            List<SmLocation> locations = locationDao.getLocationsInSmallRangeAndAccountId(accountId, latitudes[0], longitude[0], latitudes[1], longitude[1]);
            if (locations == null || locations.isEmpty()) {
                logger.debug("For account {} - No locations found for coordinates from {}, {}  to {}, {}", accountId, latitudes[0], longitude[0], latitudes[1], longitude[1]);
                continue;
            }
            //key is distance, value list of location on that distance, normally it should be a single location but theoretically it could be more
            Map<Double, List<SmLocation>> mpLocation = new TreeMap<>();
            for (SmLocation location : locations) {
                Double distance = GeoUtils.calculateDistance(location.getLatitude(), location.getLongitude(), eventsWrapper.getCoordinates().getLatitude(), eventsWrapper.getCoordinates().getLongitude());
                if (distance > maxRadius) {
                    continue;
                }
                List<SmLocation> lst = mpLocation.get(distance);
                if (lst == null) {
                    lst = new ArrayList<>();
                    mpLocation.put(distance, lst);
                }
                lst.add(location);
            }

            //creating scheduler item
            LocationScheduleItem locationScheduleItem = new LocationScheduleItem();
            ret.add(locationScheduleItem);
            locationScheduleItem.setStart(eventsWrapper.getStart());
            locationScheduleItem.setStop(locationScheduleItem.getStop());

            List<LocationScheduleItem.LocationDistance> locationDistances = new ArrayList<>();
            locationScheduleItem.setLocationDistances(locationDistances);
            for (Map.Entry<Double, List<SmLocation>> entry : mpLocation.entrySet()) {
                LocationScheduleItem.LocationDistance locationDistance = new LocationScheduleItem.LocationDistance();
                for (SmLocation smLocation : entry.getValue()) {
                    locationDistance.setLocationId(smLocation.getIdLocation());
                    locationDistance.setDistance(entry.getKey());
                    locationDistance.setLatitude(smLocation.getLatitude());
                    locationDistance.setLongitude(smLocation.getLongitude());
                    locationDistances.add(locationDistance);
                }
            }
        }

        return ret;
    }

    private Date makeDate(EventDateTime eventDateTime) {
        if (eventDateTime == null || eventDateTime.getDateTime() == null) {
            return null;
        }
        return new Date(eventDateTime.getDateTime().getValue());
    }

    private static class EventWrapper {
        private Date start;
        private Date stop;
        private Coordinates coordinates;
        private String name;

        public EventWrapper(Date start, Date stop, Coordinates coordinates, String name) {
            this.start = start;
            this.stop = stop;
            this.coordinates = coordinates;
            this.name = name;
        }

        public Date getStart() {
            return start;
        }

        public void setStart(Date start) {
            this.start = start;
        }

        public Date getStop() {
            return stop;
        }

        public void setStop(Date stop) {
            this.stop = stop;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Coordinates coordinates) {
            this.coordinates = coordinates;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class LocationWrapper {

        private double latitude;
        private double longitude;
        private Double distance;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }
    }
}
