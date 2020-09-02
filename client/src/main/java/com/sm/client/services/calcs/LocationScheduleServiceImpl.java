package com.sm.client.services.calcs;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.sm.client.model.smartcar.LocationPoint;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.SchedulerInterval;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.GoogleLocationService;
import com.sm.client.services.GoogleService;
import com.sm.client.services.SmartCarService;
import com.sm.client.services.location.optimization.IntervalOfLocation;
import com.sm.client.services.location.optimization.SimpleOptimizationService;
import com.sm.client.utils.GeoUtils;
import com.sm.dao.LocationDao;
import com.sm.dao.ResourcesDao;
import com.sm.model.Constants;
import com.sm.model.SmException;
import com.sm.model.SmLocation;
import com.sm.model.SmResource;
import com.sm.model.cache.Coordinates;
import com.sm.model.calcs.EventWrapper;
import com.sm.model.calcs.LocationWrapper;
import com.sm.model.web.LocationScheduleItem;
import com.smartcar.sdk.data.VehicleLocation;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class LocationScheduleServiceImpl implements LocationScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(LocationScheduleServiceImpl.class);

    @Autowired
    private GoogleService googleService;

    @Autowired
    private GoogleLocationService googleLocationService;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private SmartCarService smartCarService;

    @Autowired
    private ResourcesDao resourcesDao;

    @Value("${location.schedule.maxLocalRadiusInMetters:300}")
    private double maxLocalRadiusInMetters = 300;

    @Value("${location.schedule.minChargePerLocationInWatt:2000}")
    private long minChargePerLocationInWatt = 2000;

    @Override
    public SchedulerData calculate(Long accountId, VehicleData smData, SmResource smResource) throws SmException, IOException {

        Events events = googleService.getEventsForNext24hours();
        if (events == null || events.isEmpty()) {
            // no events found
            throw new SmException("No calendar events found !", HttpStatus.SC_NOT_FOUND);
        }

        EventWrapper eventWrapperLast = null;
        List<EventWrapper> eventsWrappers = new ArrayList<>();
        for (Event event : events.getItems()) {
            try {
                if (event.getLocation() == null || (event.getLocation().toUpperCase().startsWith("HTTP://") || event.getLocation().toUpperCase().startsWith("HTTPS://"))) {
                    //can't detect address for this location
                    logger.debug("-- Location {} does not look like address - it will be ignored --", event.getLocation());
                    continue;
                }
                Date stopDate = makeDate(event.getEnd());
                Date startDate = makeDate(event.getStart());

                //Looking for location coordinates
                Coordinates coordinates = googleLocationService.getLatitudeAndLongitute(event.getLocation());
                if (coordinates == null) {
                    logger.debug("-- Failed to find address location {} - it will be ignored --", event.getLocation());
                    continue;
                }
                if (eventWrapperLast != null) {
                    eventWrapperLast.setDistanceToTheNextEventLocation(GeoUtils.calculateDistance(
                            eventWrapperLast.getCoordinates().getLatitude(),
                            eventWrapperLast.getCoordinates().getLongitude(),
                            coordinates.getLatitude(),
                            coordinates.getLongitude()));
                }
                EventWrapper eventWrapper = new EventWrapper(startDate, stopDate, coordinates, event.getDescription());
                eventsWrappers.add(eventWrapper);
                eventWrapperLast = eventWrapper;

            } catch (Exception ex) {
                logger.error("Failed to process event {} ", event.toPrettyString());
            }
        }

        SchedulerData ret = new SchedulerData();
        List<SchedulerInterval> intervals = new ArrayList<>();
        ret.setIntervals(intervals);

        List<IntervalOfLocation> listForOptimization = new ArrayList<>();
        long currentEnergy = (long) (smData.getBattery().getPercentRemaining() * (double) smResource.getCapacity());
        Long needMinEnergy = minChargePerLocationInWatt;
        //now looking for cheapest location in 300m radius
        for (EventWrapper eventsWrapper : eventsWrappers) {

            double latitudes[] = GeoUtils.calculateLatRange(eventsWrapper.getCoordinates().getLatitude(), 1000);
            double longitude[] = GeoUtils.calculateLngRange(eventsWrapper.getCoordinates().getLatitude(), eventsWrapper.getCoordinates().getLongitude(), 1000);
            List<SmLocation> locations = locationDao.getLocationsInSmallRangeAndAccountId(accountId, latitudes[0], longitude[0], latitudes[1], longitude[1]);
            if (locations == null || locations.isEmpty()) {
                logger.debug("For account {} - No locations found for coordinates from {}, {}  to {}, {}", accountId, latitudes[0], longitude[0], latitudes[1], longitude[1]);
                continue;
            }
            if (eventsWrapper.getDistanceToTheNextEventLocation() != null) {
                //for the next location we need at least
                needMinEnergy = (long) (eventsWrapper.getDistanceToTheNextEventLocation() / Constants.KILLOMETER_PER_WATT);
                needMinEnergy = minChargePerLocationInWatt > needMinEnergy ? minChargePerLocationInWatt : needMinEnergy;
            }


            long timeInSec = (eventsWrapper.getStop().getTime() - eventsWrapper.getStart().getTime()) / 1000;
            //key is priceRate, value list of location on that distance, normally it should be a single location but theoretically it could be more
            Map<Double, List<LocationWrapper>> mpLocation = new TreeMap<>();
            for (SmLocation location : locations) {

                Double distance = GeoUtils.calculateDistance(location.getLatitude(), location.getLongitude(), eventsWrapper.getCoordinates().getLatitude(), eventsWrapper.getCoordinates().getLongitude());
                if (distance > maxLocalRadiusInMetters) {
                    logger.debug("By passing location {} due to maxLocalRadius limit [{} > {}]", location.getIdLocation(), distance, maxLocalRadiusInMetters);
                    continue;
                }

//                //can this location provide enough rate, to make enough energy ?
//                if (currentEnergy + ((location.getPower() > smResource.getPower()) ? smResource.getPower() : location.getPower()) * timeInSec <= needMinEnergy) {
//                    logger.debug("By passing location {} due to energy charge limit [{} > {}]", location.getIdLocation(), needMinEnergy, currentEnergy + ((location.getPower() > smResource.getPower()) ? smResource.getPower() : location.getPower()) * timeInSec);
//                    continue;
//                }

                LocationWrapper locationWrapper = new LocationWrapper(location, distance);
                List<LocationWrapper> lst = mpLocation.get(locationWrapper.getPriceRate());
                if (lst == null) {
                    lst = new ArrayList<>();
                    mpLocation.put(location.getPrice(), lst);
                }
                lst.add(locationWrapper);
            }

            //creating scheduler item
            SchedulerInterval schedulerInterval = new SchedulerInterval();
            intervals.add(schedulerInterval);
            schedulerInterval.setStarttime(eventsWrapper.getStart());
            schedulerInterval.setDuration(eventsWrapper.getStop().getTime() - eventsWrapper.getStart().getTime());
            schedulerInterval.setIntervalType(SchedulerInterval.IntervalType.DRV);

            schedulerInterval.setEventLocation(new LocationPoint(
                    null,
                    eventsWrapper.getCoordinates().getAddress(),
                    eventsWrapper.getName(),
                    eventsWrapper.getCoordinates().getLongitude(),
                    eventsWrapper.getCoordinates().getLatitude()));

            List<LocationPoint> stations = new ArrayList<>();
            schedulerInterval.setStations(stations);

            int amountOfOptions = 0;
            mlocationLevel:
            for (Map.Entry<Double, List<LocationWrapper>> entry : mpLocation.entrySet()) {
                for (LocationWrapper locationWrapper : entry.getValue()) {
                    if (stations.isEmpty()) {
                        schedulerInterval.setPrice(locationWrapper.getSmLocation().getPrice());
                        listForOptimization.add(new IntervalOfLocation(0, needMinEnergy, locationWrapper.getPriceRate(), schedulerInterval));
                    }
                    stations.add(new LocationPoint(
                            locationWrapper.getSmLocation().getIdLocation(),
                            locationWrapper.getSmLocation().getDescription(),
                            locationWrapper.getSmLocation().getName(),
                            locationWrapper.getSmLocation().getLongitude(),
                            locationWrapper.getSmLocation().getLatitude()
                    ));
                    if (amountOfOptions++ > 3) {
                        break mlocationLevel;
                    }
                }
            }
        }
        SimpleOptimizationService.clculate(listForOptimization, currentEnergy, smResource.getCapacity());
        for (IntervalOfLocation intervalOfLocation : listForOptimization) {
            SchedulerInterval schedulerInterval = ((SchedulerInterval) intervalOfLocation.getRefObject());
            schedulerInterval.setEnergy(intervalOfLocation.getCharge());
            schedulerInterval.setCostOfCharging(intervalOfLocation.getSummaryPrice());
        }
        ret.setInitialEnergy(currentEnergy);
        return ret;
    }

    @Override
    public SchedulerData calculateGeo(Long accountId, VehicleData smData, SmResource smResource) throws SmException, IOException {
        VehicleLocation vehicleLocation = smData.getLocation().getData();
        double latitudes[] = GeoUtils.calculateLatRange(vehicleLocation.getLatitude(), 15000);
        double longitude[] = GeoUtils.calculateLngRange(vehicleLocation.getLatitude(), vehicleLocation.getLongitude(), 15000);
        List<SmLocation> locations = locationDao.getLocationsInSmallRangeAndAccountId(accountId, latitudes[0], longitude[0], latitudes[1], longitude[1]);

        if (locations == null || locations.isEmpty()) {
            throw new SmException("Can't find any location near point " + vehicleLocation.getLatitude() + "," + vehicleLocation.getLongitude() + " within 10 mills", HttpStatus.SC_NOT_FOUND);
        }


        long currentEnergy = (long) (smData.getBattery().getPercentRemaining() * (double) smResource.getCapacity());

        TreeMap<Double, LocationWrapper> locationsMap = new TreeMap<>();
        TreeMap<Double, LocationWrapper> locationsMinDistanceMap = new TreeMap<>();

        for (SmLocation smLocation : locations) {

            //calculating distance to location
            Double distance = GeoUtils.calculateDistance(vehicleLocation.getLatitude(), vehicleLocation.getLongitude(), smLocation.getLatitude(), smLocation.getLongitude());

            locationsMinDistanceMap.put(distance, new LocationWrapper(smLocation, distance));

            if (smLocation.getPrice() == null || smLocation.getPrice() == 0) {
                continue;
            }

            long needMinEnergy = (long) (distance / Constants.KILLOMETER_PER_WATT);
            double cost = (smResource.getCapacity() - (currentEnergy - needMinEnergy)) * smLocation.getPrice();
            LocationWrapper locationWrapper = new LocationWrapper(smLocation, distance);
            locationWrapper.setPriceRate(smLocation.getPrice());
            locationsMap.put(cost, locationWrapper);
        }

        //finding the first location which will be preferred
        Map.Entry<Double, LocationWrapper> prefEntry = !locationsMap.isEmpty() ? locationsMap.entrySet().iterator().next() : locationsMinDistanceMap.isEmpty() ? null : locationsMinDistanceMap.entrySet().iterator().next();
        if (prefEntry == null) {
            throw new SmException("No locations found !", HttpStatus.SC_NOT_FOUND);
        }
        double preferredCost = prefEntry.getKey();
        LocationWrapper preferredLocationWrapper = prefEntry.getValue();
        SmLocation preferredLocation = preferredLocationWrapper.getSmLocation();

        List<LocationPoint> stations = new ArrayList<>();
        for (LocationWrapper locationWrapper : locationsMap.values()) {
            SmLocation location = locationWrapper.getSmLocation();
            stations.add(new LocationPoint(location.getIdLocation(), location.getDescription(), location.getName(), location.getLongitude(), location.getLatitude()));
        }

        if (stations.size() < 3) {
            for (LocationWrapper locationWrapper : locationsMinDistanceMap.values()) {
                SmLocation location = locationWrapper.getSmLocation();
                stations.add(new LocationPoint(location.getIdLocation(), location.getDescription(), location.getName(), location.getLongitude(), location.getLatitude()));
            }
        }

        //generating schedule
        SchedulerData ret = new SchedulerData();

        //creating scheduler item
        SchedulerInterval schedulerInterval = new SchedulerInterval();
        ret.setIntervals(Arrays.asList(schedulerInterval));

        schedulerInterval.setIntervalType(SchedulerInterval.IntervalType.DRV);

        schedulerInterval.setStations(stations);
        schedulerInterval.setPrice(preferredLocation.getPrice());
        schedulerInterval.setChargeRate(preferredLocation.getPower());

        //calculating time which we need to reach location
        long timeToLocation = (long) ((3600D * preferredLocationWrapper.getDistance() / 30000D) * 1000D);
        schedulerInterval.setStarttime(new Date(System.currentTimeMillis() + timeToLocation));

        schedulerInterval.setDuration((long) (3600000 * preferredCost / preferredLocation.getPrice()));

        return ret;
    }

    private Date makeDate(EventDateTime eventDateTime) {
        if (eventDateTime == null || eventDateTime.getDateTime() == null) {
            return null;
        }
        return new Date(eventDateTime.getDateTime().getValue());
    }

    public void setGoogleService(GoogleService googleService) {
        this.googleService = googleService;
    }

    public void setGoogleLocationService(GoogleLocationService googleLocationService) {
        this.googleLocationService = googleLocationService;
    }

    public void setLocationDao(LocationDao locationDao) {
        this.locationDao = locationDao;
    }


}
