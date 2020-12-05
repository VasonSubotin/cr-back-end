package com.sm.client.services;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.cache.VehiclesCache;
import com.sm.client.utils.TestLocations;
import com.sm.dao.AccountsDao;
import com.sm.dao.ResourcesDao;
import com.sm.dao.UserSessionDao;
import com.sm.dao.cache.SmartCarCacheDao;
import com.sm.model.*;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartCarService {

    private Logger logger = LoggerFactory.getLogger(SmartCarService.class);

    private AuthClient client;

    @Value("${smartcar.clientId:#{null}}")
    private String clientId;

    @Value("${smartcar.clientSecret:#{null}}")
    private String clientSecret;

    @Value("${smartcar.redirectUrl:http://localhost:8080/smartCarToken}")
    private String urlRedirect;

    @Value("#{'${smartcar.permissions:required:read_vehicle_info,read_odometer,read_engine_oil,read_battery,read_charge,read_fuel,read_location,control_security,read_tires,read_vin}'.split(',')}")
    private String permissions[];

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SmartCarCacheDao smartCarCacheDao;



    @Value("${smartcar.testMode:false}")
    private boolean testMode = false;


    @PostConstruct
    public void init() {
        this.client = new AuthClient(
                clientId,
                clientSecret,
                urlRedirect,
                permissions,
                testMode
        );
        objectMapper
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    public AuthClient getClient() {
        return client;
    }


//    public VehicleData getVehicleData(SmUserSession userSession, SmResource smResource) throws SmException {
//        try {
//            Pair<SmUserSession, SmartcarResponse<VehicleIds>> p = getVehicleIds(userSession);
//            userSession = p.getKey();
//            if (userSession == null) {
//                throw new SmException("No active smart car session found for login " + SecurityContextHolder.getContext().getAuthentication().getName(), HttpStatus.SC_FORBIDDEN);
//            }
//            SmartcarResponse<VehicleIds> vehicleIdResponse = p.getValue(); //AuthClient.getVehicleIds(userSession.getToken());
//            for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
//                Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
//                String vId = vehicle.vin();
//                if (vId.equals(smResource.getExternalResourceId())) {
//                    return getSingleData(vehicle, vId, new ArrayList<>());
//                }
//            }
//        } catch (SmartcarException e) {
//            logger.error(e.getMessage(), e);
//            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
//        }
//        return null;
//    }

    public Pair<SmUserSession, SmartcarResponse<VehicleIds>> getVehicleIds(SmUserSession userSession) throws SmartcarException, SmException {
        try {
            return new Pair<>(userSession, AuthClient.getVehicleIds(userSession.getToken()));
        } catch (SmartcarException e) {
            //
            if (e.getMessage().contains("expired")) {
                //refreshing tokens
                Auth auth = client.exchangeRefreshToken(userSession.getRefreshToken());
                userSession = securityService.updateCurrentSession(userSession.getToken(), Constants.SMART_CAR_AUTH_TYPE, auth.getAccessToken(), auth.getRefreshToken(), 3600000);
                return new Pair<>(userSession, AuthClient.getVehicleIds(userSession.getToken()));
            }
        }
        return new Pair<>(userSession, null);
    }

    public boolean needInitUserSession(Long resourceId) throws SmException {
        try {
            //new mode
            SmUserSession userSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE, resourceId);
            if (userSession == null) {
                return true;
            }
            getVehicleIds(userSession);
            return false;
        } catch (SmartcarException ex) {
            return true;
        }
    }

    public Map<Long, Boolean> needInitUserSessions() throws SmException {

        //new mode
        Map<Long, Boolean> ret = new HashMap<>();
        List<SmUserSession> lstSessions = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);
        //SmUserSession userSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE, resourceId);
        List<SmResource> lstResources = resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount());
        Set<Long> uniqResources = lstResources.stream().map(SmResource::getIdResource).collect(Collectors.toSet());
        if (lstSessions == null) {
            for (Long ressourceId : uniqResources) {
                ret.put(ressourceId, true);
            }
            return ret;
        }

        Map<String, Boolean> tokens = new HashMap<>();
        for (SmUserSession smUserSession : lstSessions) {
            if (!uniqResources.contains(smUserSession.getResourceId())) {
                continue;
            }
            Boolean status = tokens.get(smUserSession.getToken());
            if (status == null) {
                try {
                    getVehicleIds(smUserSession);
                    ret.put(smUserSession.getResourceId(), false);
                    tokens.put(smUserSession.getToken(), false);
                } catch (SmartcarException ex) {
                    ret.put(smUserSession.getResourceId(), true);
                    tokens.put(smUserSession.getToken(), true);
                }
            } else {
                ret.put(smUserSession.getResourceId(), status);
            }
        }

        return ret;
    }

    public VehicleData getSingleData(Vehicle vehicle, String vin, List<SmTiming> timers) throws SmException {
        if (testMode) {
            return getSingleDataNonBatch(vehicle, vin, timers);
        } else {
            return getSingleDataBatch(vehicle, vin, timers);
        }
    }


    public VehicleData getSingleDataBatch(Vehicle vehicle, String vin, List<SmTiming> timers) throws SmException {
        VehicleData vehicleData = new VehicleData();
        long start = System.currentTimeMillis();
        try {
            BatchResponse batchResponse = vehicle.batch(new String[]{"/battery", "/location", "/charge", "/"});
            if (testMode) {
                vehicleData.setLocation(TestLocations.getTestCarLocationByVin(vin));
            } else {
                vehicleData.setLocation(batchResponse.location());
            }
            vehicleData.setVehicleInfo(batchResponse.info());// will be root /
            vehicleData.setCharge(batchResponse.charge());
            vehicleData.setBattery(batchResponse.battery().getData());

        } catch (Exception ex) {
            logger.error("Failed to get batch info for {} due to error : {} - will use default values", vin, ex.getMessage(), ex);
            throw new SmException(ex.getMessage(), ex, 500);
        } finally {
            timers.add(new SmTiming("vehicle.batch() " + vin, System.currentTimeMillis() - start));
        }
        vehicleData.setVin(vin);
        vehicleData.setVehicleId(vehicle.getVehicleId());
        return vehicleData;
    }

    private VehicleData getSingleDataNonBatch(Vehicle vehicle, String vin, List<SmTiming> timers) {
        VehicleData vehicleData = new VehicleData();
        long start = System.currentTimeMillis();
        try {
            vehicleData.setBattery(vehicle.battery().getData());
        } catch (Exception ex) {
            logger.error("ailed to get battery info for {} due to error : {} - will use default value 50%", ex.getMessage(), ex);
            vehicleData.setBattery(new VehicleBattery(100D, 50D));
        } finally {
            timers.add(new SmTiming("vehicle.battery().getData() " + vin, System.currentTimeMillis() - start));
        }

        try {
            start = System.currentTimeMillis();
            vehicleData.setVehicleInfo(vehicle.info());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            timers.add(new SmTiming("vehicle.info()  " + vin, System.currentTimeMillis() - start));
        }


        try {
            start = System.currentTimeMillis();
            vehicleData.setCharge(vehicle.charge());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            timers.add(new SmTiming("vehicle.charge() " + vin, System.currentTimeMillis() - start));
        }


        try {
            start = System.currentTimeMillis();
            vehicleData.setVin(vin);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            timers.add(new SmTiming("vin " + vin, System.currentTimeMillis() - start));
        }

        try {
            start = System.currentTimeMillis();
            vehicleData.setVehicleId(vehicle.getVehicleId());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            timers.add(new SmTiming("vehicle.getVehicleId() " + vin, System.currentTimeMillis() - start));
        }

        try {
            start = System.currentTimeMillis();
            if (testMode) {
                vehicleData.setLocation(TestLocations.getTestCarLocationByVin(vin));
            } else {
                vehicleData.setLocation(vehicle.location());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            timers.add(new SmTiming("vehicle.location() " + vin, System.currentTimeMillis() - start));
        }

        return vehicleData;
    }


}
