package com.sm.client.services;

import com.sm.client.model.smartcar.VehicleData;
import com.sm.dao.*;
import com.sm.model.*;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.SmartcarResponse;
import com.smartcar.sdk.data.VehicleIds;
import com.smartcar.sdk.data.VehicleLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is not used yet, it is a prototype of events
 */

@Service
public class EventGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(EventGeneratorService.class);

    @Autowired
    private EventsDao eventsDao;

    @Autowired
    private UserSessionDao userSessionDao;

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private SessionsDao sessionsDao;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SmartCarService smartCarService;


    public void generateEvent(Long accountId) throws SmartcarException {

        List<SmUserSession> smUserSessions = userSessionDao.getSessionsByType(accountId, Constants.SMART_CAR_AUTH_TYPE);
        SmUserSession smUserSession = smUserSessions.get(0);
        String token = smUserSession.getToken();

        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(token);

        //building cache for resources
        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(accountId);

        Map<String, SmResource> resurcesMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a, (n, o) -> n));

        //refreshing resources just in case
        //smartCarService.refresh(resources, vehicleIdResponse, smUserSession, null);

        for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
            VehicleData vehicleData = new VehicleData();
            Vehicle vehicle = new Vehicle(vehicleId, token);

            String vin = vehicle.vin();

            SmResource smResource = resurcesMap.get(vin);
            if (smResource == null) {
                //no such resource in system - internal error?
                logger.info("For account id {} No resources for vin {} -- will be ignored", accountId, vin);
            }
            try {
                vehicleData.setBattery(vehicle.battery().getData());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setVehicleInfo(vehicle.info());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setOdometer(vehicle.odometer());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setCharge(vehicle.charge());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setFuel(vehicle.fuel());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setOil(vehicle.oil());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setVin(vin);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setVehicleId(vehicle.getVehicleId());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setLocation(vehicle.location());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setTirePressure(vehicle.tirePressure());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            EventData eventData = new EventData(vehicleData, smResource, smUserSession);


            //smEvent.setExtTemperature(vehicleData.getCharge().getData().);
            //vehiclesList.add(vehicleData);
        }

    }

    public void processEvent(EventData eventData) {
        VehicleData vehicleData = eventData.getVehicleData();
        SmResource smResource = eventData.getSmResource();

        SmEvent smEvent = new SmEvent();
        smEvent.setLatitude(vehicleData.getLocation().getData().getLatitude());
        smEvent.setLongitude(vehicleData.getLocation().getData().getLongitude());

        Long accountId = eventData.getSmResource().getAccountId();
        smEvent.setAccountId(accountId);
        smEvent.setEnergy(Double.valueOf(vehicleData.getBattery().getPercentRemaining()).longValue());

        vehicleData.getCharge().getData().getState();
        vehicleData.getCharge().getData().getIsPluggedIn();
    }


    private void plugIn(EventData eventData) {
        // checking if user has session
        SmSession smSession = sessionsDao.getActiveSessionByAccountIdAndResourceId(eventData.getSmResource().getAccountId(), eventData.getSmResource().getPolicyId());
        if (smSession != null) {
            smSession.setDtStop(new Date());
            smSession.setClosed(true);
            smSession.setDuration(smSession.getDtStop().getTime() - smSession.getDtStart().getTime());
            sessionsDao.saveSession(smSession);
        }

        //starting new session -
        smSession = createSession(eventData);
        sessionsDao.saveSession(smSession);
    }

    private void plugOut(EventData eventData) {
        // checking if user has session
        SmSession smSession = sessionsDao.getActiveSessionByAccountIdAndResourceId(eventData.getSmResource().getAccountId(), eventData.getSmResource().getPolicyId());
        if (smSession == null) {
            smSession = createSession(eventData);
            smSession.setDtStop(new Date());
        }
        smSession.setClosed(true);
        sessionsDao.saveSession(smSession);

        smSession = createSession(eventData);
        // new non charge session
        sessionsDao.saveSession(smSession);
    }

    private void startCharge(EventData eventData) {
        SmSession smSession = sessionsDao.getActiveSessionByAccountIdAndResourceId(eventData.getSmResource().getAccountId(), eventData.getSmResource().getPolicyId());
        if (smSession == null) {
            smSession = createSession(eventData);
        }
        sessionsDao.saveSession(smSession);
    }

    private void stopCharge(EventData eventData) {
        SmSession smSession = sessionsDao.getActiveSessionByAccountIdAndResourceId(eventData.getSmResource().getAccountId(), eventData.getSmResource().getPolicyId());
        if (smSession == null) {
            smSession = createSession(eventData);
        }
        sessionsDao.saveSession(smSession);
    }


    private SmSession createSession(EventData eventData) {

        SmSession smSession = new SmSession();
        smSession.setAccountId(eventData.getSmResource().getAccountId());
        smSession.setResourceId(eventData.getSmResource().getIdResource());
        smSession.setCarbonImpact(0D);
        smSession.setDuration(0L);
        smSession.setEnergy(0L);
        smSession.setCarbonSavings(0D);
        smSession.setSessionTypeId(1L);

        smSession.setDtStart(new Date());
        smSession.setClosed(false);

        VehicleLocation location = eventData.getVehicleData().getLocation().getData();
        smSession.setLatitude(location.getLatitude());
        smSession.setLongitude(location.getLongitude());

        return smSession;
    }

    public void globalEventScan() {
        // for accounts will try to get data and generate events
        for (SmAccount smAccount : accountsDao.getAllAccounts()) {
            try {
                generateEvent(smAccount.getIdAccount());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }


    private class EventData implements Serializable {
        private VehicleData vehicleData;
        private SmResource smResource;
        private SmUserSession smUserSession;

        public EventData() {
        }

        public EventData(VehicleData vehicleData, SmResource smResource, SmUserSession smUserSession) {
            this.vehicleData = vehicleData;
            this.smResource = smResource;
            this.smUserSession = smUserSession;
        }

        public VehicleData getVehicleData() {
            return vehicleData;
        }

        public void setVehicleData(VehicleData vehicleData) {
            this.vehicleData = vehicleData;
        }

        public SmResource getSmResource() {
            return smResource;
        }

        public void setSmResource(SmResource smResource) {
            this.smResource = smResource;
        }

        public SmUserSession getSmUserSession() {
            return smUserSession;
        }

        public void setSmUserSession(SmUserSession smUserSession) {
            this.smUserSession = smUserSession;
        }
    }
}
