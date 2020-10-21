package com.sm.client.services;


import com.sm.client.model.smartcar.SmResourceState;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.cache.VehiclesCache;
import com.sm.client.utils.TestLocations;
import com.sm.dao.AccountsDao;
import com.sm.dao.ResourcesDao;

import com.sm.model.*;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.*;


import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartCarService {

    private Logger logger = LoggerFactory.getLogger(SmartCarService.class);

    private AuthClient client;
    // private String code;
    // private String access;

    @Value("${smartcar.clientId:#{null}}")
    private String clientId;

    @Value("${smartcar.clientSecret:#{null}}")
    private String clientSecret;

    @Value("${smartcar.redirectUrl:http://localhost:8080/smartCarToken}")
    private String urlRedirect;

    @Value("#{'${smartcar.permissions:required:read_vehicle_info,read_odometer,read_engine_oil,read_battery,read_charge,read_fuel,read_location,control_security,read_tires,read_vin}'.split(',')}")
    private String permissions[];


    @Autowired
    private VehiclesCache vehiclesCache;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private ResourcesDao resourcesDao;

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
    }

    public AuthClient getClient() {
        return client;
    }

    public void refreshCarData(String login) throws SmException, SmartcarException {
        refreshCarData(login, null);
    }

    public void refreshCarData(String login, Map<String, VehicleLocation> locationMap) throws SmException, SmartcarException {
        //need to getResources from smartCar
        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount());

        SmUserSession userSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);

        if (userSession == null) {
            throw new SmException("No active smart car session found for user " + login, HttpStatus.SC_FORBIDDEN);
        }


        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(userSession.getToken());

        refresh(resources, vehicleIdResponse, userSession, locationMap);
    }

    public void refresh(List<SmResource> resources, SmartcarResponse<VehicleIds> vehicleIdResponse, SmUserSession userSession, Map<String, VehicleLocation> locationMap) throws SmartcarException {
        Map<String, SmResource> resourceMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a, (n, o) -> n));
        Map<String, SmResource> needToSave = new HashMap<>();

        List<Pair<SmResource, String>> ret = new ArrayList<>();

        for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
            Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
            String vId = vehicle.vin();

            SmResource smResource = resourceMap.get(vId);
            if (smResource == null) {
                smResource = new SmResource();
                smResource.setResourceTypeId(1L);
                smResource.setDtCreated(new Date());
                smResource.setExternalResourceId(vId);
                smResource.setAccountId(userSession.getAccountId());
                smResource.setPolicyId(1L);
                needToSave.put(vId, smResource);
                resourceMap.put(vId, smResource);
            }

            if (locationMap != null) {
                try {
                    locationMap.put(smResource.getExternalResourceId(), vehicle.location().getData());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }

            try {
                VehicleInfo vehicleInfo = vehicle.info();

                smResource.setModel(vehicleInfo.getModel());
                smResource.setVendor(vehicleInfo.getMake());
                needToSave.put(vId, smResource);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicle.charge();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        for (SmResource smResource : needToSave.values()) {
            smResource.setDtUpdated(new Date());
            setBattery(smResource);
            resourcesDao.saveResource(smResource, userSession.getAccountId());
        }
    }

    public VehicleData getVehicleData(SmUserSession userSession, SmResource smResource) throws SmException {
        try {
            Pair<SmUserSession, SmartcarResponse<VehicleIds>> p = getVehicleIds(userSession);
            userSession = p.getKey();
            if (userSession == null) {
                throw new SmException("No active smart car session found for login " + SecurityContextHolder.getContext().getAuthentication().getName(), HttpStatus.SC_FORBIDDEN);
            }
            SmartcarResponse<VehicleIds> vehicleIdResponse = p.getValue(); //AuthClient.getVehicleIds(userSession.getToken());
            for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
                Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
                String vId = vehicle.vin();
                if (vId.equals(smResource.getExternalResourceId())) {
                    return getSingleData(vehicle, vId, new ArrayList<>());
                }
            }
        } catch (SmartcarException e) {
            logger.error(e.getMessage(), e);
            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
        }
        return null;
    }

    private Pair<SmUserSession, SmartcarResponse<VehicleIds>> getVehicleIds(SmUserSession userSession) throws SmartcarException, SmException {
        try {
            return new Pair<>(userSession, AuthClient.getVehicleIds(userSession.getToken()));
        } catch (SmartcarException e) {
            //
            if (e.getMessage().contains("expired")) {
                //refreshing tokens
                Auth auth = client.exchangeRefreshToken(userSession.getRefreshToken());
                userSession = securityService.saveCurrentSession(Constants.SMART_CAR_AUTH_TYPE, auth.getAccessToken(), auth.getRefreshToken(), 3600000);
                return new Pair<>(userSession, AuthClient.getVehicleIds(userSession.getToken()));
            }
        }
        return new Pair<>(userSession, null);
    }

    public boolean needInitUserSession() throws SmException {
        try {
            SmUserSession userSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);
            getVehicleIds(userSession);
            return false;
        } catch (SmartcarException ex) {
            return true;
        }
    }

    public List<SmResourceState> getResourceState(String login) throws SmException {
        List<SmTiming> timers = new ArrayList<>();
        long start = System.currentTimeMillis();
        SmUserSession userSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);
        timers.add(new SmTiming("getActiveSessionByLogin", System.currentTimeMillis() - start));

        if (userSession == null) {
            throw new SmException("No active smart car session found for user " + login, HttpStatus.SC_FORBIDDEN);
        }
        start = System.currentTimeMillis();
        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(userSession.getAccountId());
        timers.add(new SmTiming("getAllResourceByAccountId", System.currentTimeMillis() - start));
        Map<String, SmResource> resourceMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a, (n, o) -> n));

        List<SmResourceState> ret = new ArrayList<>();
        Set<String> unUsedVins = new HashSet<>(resourceMap.keySet());
        try {
            start = System.currentTimeMillis();
            SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(userSession.getToken());
            timers.add(new SmTiming("getVehicleIds", System.currentTimeMillis() - start));
            for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
                start = System.currentTimeMillis();
                Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
                timers.add(new SmTiming("new Vehicle " + vehicleId, System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
                String vId = vehicle.vin();
                timers.add(new SmTiming("get vin " + vId, System.currentTimeMillis() - start));
                start = System.currentTimeMillis();
                SmResource resource = resourceMap.get(vId);
                if (resource != null) {
                    ret.add(new SmResourceState(getSingleData(vehicle, vId, timers), resource));
                    unUsedVins.remove(vId);
                }
                timers.add(new SmTiming("resourceMap.get  " + vId, System.currentTimeMillis() - start));
            }
            start = System.currentTimeMillis();
            for (String vId : unUsedVins) {
                SmResource resource = resourceMap.get(vId);
                ret.add(new SmResourceState(null, resource));
            }
            timers.add(new SmTiming("resourceMap.get  ", System.currentTimeMillis() - start));
            if (!ret.isEmpty()) {
                ret.get(0).setTimers(timers);
            } else {
                SmResourceState rs = new SmResourceState(null, null);
                rs.setTimers(timers);
                ret.add(rs);
            }

        } catch (SmartcarException e) {
            logger.error(e.getMessage(), e);
            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
        }
        return ret;
    }

    public SmResourceState getResourceState(String login, Long resourceId) throws SmException {
        SmUserSession userSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);

        if (userSession == null) {
            throw new SmException("No active smart car session found for user " + login, HttpStatus.SC_FORBIDDEN);
        }

        SmResource resource = resourcesDao.getResourceByIdAndAccountId(resourceId, userSession.getAccountId());

        try {
            Pair<SmUserSession, SmartcarResponse<VehicleIds>> p = getVehicleIds(userSession);
            userSession = p.getKey();
            SmartcarResponse<VehicleIds> vehicleIdResponse = p.getValue(); //AuthClient.getVehicleIds(userSession.getToken());
            for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
                Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
                if (resource.getExternalResourceId().equals(vehicle.vin())) {
                    List<SmTiming> timers = new ArrayList<>();
                    SmResourceState smResourceState = new SmResourceState(getSingleData(vehicle, vehicle.vin(), timers), resource);
                    smResourceState.setTimers(timers);
                    return smResourceState;
                }
            }
        } catch (SmartcarException e) {
            logger.error(e.getMessage(), e);
            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
        }
        return new SmResourceState(null, resource);
    }

    private VehicleData getSingleData(Vehicle vehicle, String vin, List<SmTiming> timers) {
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
            logger.error("Failed to get batch info for {} due to error : {} - will use default values", ex.getMessage(), ex);
            vehicleData.setVehicleId(vehicle.getVehicleId());
        } finally {
            timers.add(new SmTiming("vehicle.batch() " + vin, System.currentTimeMillis() - start));
        }
        vehicleData.setVin(vin);
        vehicleData.setVehicleId(vehicle.getVehicleId());
        return vehicleData;
    }

    private VehicleData getSingleData2(Vehicle vehicle, String vin, List<SmTiming> timers) {
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

//        try {
//            start = System.currentTimeMillis();
//            vehicleData.setOdometer(vehicle.odometer());
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }finally{
//            timers.add(new SmTiming("vehicle.odometer()  " + vin, System.currentTimeMillis() - start));
//        }


        try {
            start = System.currentTimeMillis();
            vehicleData.setCharge(vehicle.charge());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            timers.add(new SmTiming("vehicle.charge() " + vin, System.currentTimeMillis() - start));
        }

//        try {
//            start = System.currentTimeMillis();
//            vehicleData.setFuel(vehicle.fuel());
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }finally{
//            timers.add(new SmTiming("vehicle.fuel() " + vin, System.currentTimeMillis() - start));
//        }

//        try {
//            start = System.currentTimeMillis();
//            vehicleData.setOil(vehicle.oil());
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }finally{
//            timers.add(new SmTiming("vehicle.oil() " + vin, System.currentTimeMillis() - start));
//        }

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

//        try {
//            start = System.currentTimeMillis();
//            vehicleData.setTirePressure(vehicle.tirePressure());
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//        }finally{
//            timers.add(new SmTiming("vehicle.tirePressure() " + vin, System.currentTimeMillis() - start));
//        }
        return vehicleData;
    }

    private void setBattery(SmResource smResource) {
        List<VehicleModel> lst = vehiclesCache.getModels(smResource.getVendor(), smResource.getModel());
        if (lst == null || lst.isEmpty()) {
            return;
        }
        smResource.setCapacity(lst.iterator().next().getBattery());
    }
}
