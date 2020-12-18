package com.sm.client.services.calcs;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.sm.client.model.smartcar.*;
import com.sm.client.services.GoogleLocationService;
import com.sm.client.services.GoogleService;
import com.sm.client.services.SmartCarService;
import com.sm.client.services.location.optimization.IntervalOfLocation;
import com.sm.client.services.location.optimization.SimpleOptimizationService;
import com.sm.client.utils.GeoUtils;
import com.sm.dao.LocationDao;
import com.sm.dao.ResourcesDao;
import com.sm.model.*;
import com.sm.model.cache.Coordinates;
import com.sm.model.calcs.EventWrapper;
import com.sm.model.calcs.LocationWrapper;
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

        List<EventWrapper> eventsWrappers = getEvents();

        SchedulerData ret = new SchedulerData();
        List<SchedulerInterval> intervals = new ArrayList<>();
        ret.setIntervals(intervals);

        long currentEnergy = calculateInitialEnergy(eventsWrappers, smData, smResource);

        //now looking for cheapest location in 300m radius
        List<IntervalOfLocation> listForOptimization = prepareForCalculation(accountId, eventsWrappers, intervals);
        SimpleOptimizationService.clculate(listForOptimization, currentEnergy, smResource.getCapacity());

        long endEnergy = currentEnergy;
        for (IntervalOfLocation intervalOfLocation : listForOptimization) {
            SchedulerInterval schedulerInterval = ((SchedulerInterval) intervalOfLocation.getRefObject());
            schedulerInterval.setEnergy(intervalOfLocation.getCharge());
            schedulerInterval.setCostOfCharging(intervalOfLocation.getSummaryPrice());
            endEnergy = endEnergy + intervalOfLocation.getCharge() - intervalOfLocation.getNeedEnergy();
        }
        if (ret.getIntervals() != null && !ret.getIntervals().isEmpty()) {
            ret.setTimeStart(ret.getIntervals().get(0).getStartTime());
            SchedulerInterval schedulerIntervalLast = ret.getIntervals().get(ret.getIntervals().size() - 1);
            ret.setTimeStop(new Date(schedulerIntervalLast.getStartTime().getTime() + schedulerIntervalLast.getDuration()));
        }
        ret.setInitialEnergy(currentEnergy);
        ret.setAccountId(accountId);
        ret.setResourceId(smResource.getIdResource());
        ret.setPolicyId(smResource.getPolicyId());
        ret.setCreatedTime(new Date());
        ret.setScheduleType(SmScheduleType.DRV);
        ret.setCapacity(smResource.getCapacity());
        if (smResource.getCapacity() != null && smResource.getCapacity() > 0) {
            ret.setEndSoc(100.0 * endEnergy / (double) smResource.getCapacity());
        }
        return ret;
    }

    private long calculateInitialEnergy(List<EventWrapper> events, VehicleData smData, SmResource smResource) {

        long currentEnergy = (long) (smData.getBattery().getPercentRemaining() * (double) smResource.getCapacity());
        if (events == null || events.isEmpty()) {
            return currentEnergy;
        }
        EventWrapper firstEvent = events.get(0);//first event

        // current location
        VehicleLocation vLocation = smData.getLocation().getData();
        Double distance = GeoUtils.calculateDistance(vLocation.getLatitude(), vLocation.getLongitude(), firstEvent.getCoordinates().getLatitude(), firstEvent.getCoordinates().getLongitude());
        // checking if we have time to make extra charge (at least 30 mins )
        long timeInMilliSecToReachFirstLocation = (long) (distance * 3600_000D / 30_000D);
        if (firstEvent.getStart().getTime() - System.currentTimeMillis() > timeInMilliSecToReachFirstLocation + 30 * 60_000) {
            // will create extra event with current location to charge
            EventWrapper eventWrapper = new EventWrapper(new Date(), new Date(firstEvent.getStart().getTime() - timeInMilliSecToReachFirstLocation),
                    new Coordinates("Current location", firstEvent.getCoordinates().getLatitude(), firstEvent.getCoordinates().getLongitude()), "Initial charge");

            events.add(0, eventWrapper);
        }
        long initialEnergy = currentEnergy - (long) (distance * Constants.METER_PER_WATT);
        logger.info("Got initial energy {} " + (initialEnergy > 0 ? "" : " the negative value means we don't have enoth energy to reach the first location, so we will use initial value as 0"), initialEnergy);
        return initialEnergy < 0 ? 0 : initialEnergy;

    }

    private List<EventWrapper> getEvents() throws SmException, IOException {

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
                EventWrapper eventWrapper = new EventWrapper(startDate, stopDate, coordinates, event.getSummary() + (event.getDescription() == null ? "" : ":" + event.getDescription()));
                eventsWrappers.add(eventWrapper);
                eventWrapperLast = eventWrapper;

            } catch (Exception ex) {
                logger.error("Failed to process event {} ", event.toPrettyString());
            }
        }
        return eventsWrappers;
    }

    private List<IntervalOfLocation> prepareForCalculation(Long accountId, List<EventWrapper> eventsWrappers, List<SchedulerInterval> intervals) {
        List<IntervalOfLocation> listForOptimization = new ArrayList<>();
        Long needMinEnergy = minChargePerLocationInWatt;
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
                needMinEnergy = (long) (eventsWrapper.getDistanceToTheNextEventLocation() * Constants.METER_PER_WATT);
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
            schedulerInterval.setStartTime(eventsWrapper.getStart());
            schedulerInterval.setDuration(eventsWrapper.getStop().getTime() - eventsWrapper.getStart().getTime());
            schedulerInterval.setSmScheduleType(SmScheduleType.DRV);

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
                        schedulerInterval.setChargeRate(locationWrapper.getSmLocation().getPower());
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
        return listForOptimization;
    }

    @Override
    public SchedulerData calculateGeo(Long accountId, VehicleData smData, SmResource smResource) throws SmException, IOException {
        if (smData.getLocation() == null || smData.getLocation().getData() == null) {
            throw new SmException("No location was return by Smart Car service for recource id=" + smResource.getIdResource(), HttpStatus.SC_NOT_FOUND);
        }
        VehicleLocation vehicleLocation = smData.getLocation().getData();
        double latitudes[] = GeoUtils.calculateLatRange(vehicleLocation.getLatitude(), 15000);
        double longitude[] = GeoUtils.calculateLngRange(vehicleLocation.getLatitude(), vehicleLocation.getLongitude(), 15000);
        List<SmLocation> locations = locationDao.getLocationsInSmallRangeAndAccountId(accountId, latitudes[0], longitude[0], latitudes[1], longitude[1]);

        if (locations == null || locations.isEmpty()) {
            throw new SmException("Can't find any location near point " + vehicleLocation.getLatitude() + "," + vehicleLocation.getLongitude() + " within 10 mills", HttpStatus.SC_NOT_FOUND);
        }

        if (smResource.getCapacity() == null) {
            throw new SmException("Can't find capacity for resourceId=" + smResource.getIdResource(), HttpStatus.SC_NOT_FOUND);
        }

        if (smData.getBattery() == null ) {
            throw new SmException("Can't find Battery status for resourceId=" + smResource.getIdResource(), HttpStatus.SC_NOT_FOUND);
        }

        long currentEnergy = (long) (smData.getBattery().getPercentRemaining() * (double) smResource.getCapacity());

        TreeMap<Double, LocationWrapper> locationsMap = new TreeMap<>();
        TreeMap<Double, LocationWrapper> locationsMinDistanceMap = new TreeMap<>();

        for (SmLocation smLocation : locations) {
            if (smLocation.getPower() == null) {
                smLocation.setPower(Constants.DEFAULT_POWER_WATT);
            }
            //calculating distance to location
            Double distance = GeoUtils.calculateDistance(vehicleLocation.getLatitude(), vehicleLocation.getLongitude(), smLocation.getLatitude(), smLocation.getLongitude());

            locationsMinDistanceMap.put(distance, new LocationWrapper(smLocation, distance));

            if (smLocation.getPrice() == null || smLocation.getPrice() == 0) {
                continue;
            }

            long needMinEnergy = (long) (distance * Constants.METER_PER_WATT);
            double cost = (smResource.getCapacity() - (currentEnergy - needMinEnergy)) * smLocation.getPrice() / 1000;
            LocationWrapper locationWrapper = new LocationWrapper(smLocation, distance);
            locationWrapper.setPriceRate(smLocation.getPrice());
            locationWrapper.setEnergy(needMinEnergy);
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
            if (stations.size() > 3) {
                break;
            }
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

        schedulerInterval.setSmScheduleType(SmScheduleType.DRV);
        ret.setScheduleType(SmScheduleType.DRV);

        schedulerInterval.setStations(stations);
        schedulerInterval.setPrice(preferredLocation.getPrice());
        schedulerInterval.setChargeRate(preferredLocation.getPower());
        schedulerInterval.setEnergy((smResource.getCapacity() - (currentEnergy - preferredLocationWrapper.getEnergy())));
        //calculating time which we need to reach location with 30km speed in direction without road
        long timeToLocation = (long) ((3600D * preferredLocationWrapper.getDistance() / 30000D) * 1000D);
        schedulerInterval.setStartTime(new Date(System.currentTimeMillis() + timeToLocation));
        //in mills
        schedulerInterval.setDuration((long) ((double) schedulerInterval.getEnergy() / (double) schedulerInterval.getChargeRate() * 3600_000D));
        schedulerInterval.setCostOfCharging(preferredCost);

        ret.setInitialEnergy(currentEnergy);
        ret.setResourceId(smResource.getIdResource());
        ret.setAccountId(smResource.getAccountId());
        ret.setTimeStart(schedulerInterval.getStartTime());
        ret.setCapacity(smResource.getCapacity());
        ret.setTimeStop(new Date(ret.getTimeStart().getTime() + schedulerInterval.getDuration()));
        ret.setPolicyId(smResource.getPolicyId());
        // ret.setSessionType(SmSessionType);
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
