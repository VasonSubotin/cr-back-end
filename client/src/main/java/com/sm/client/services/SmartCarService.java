package com.sm.client.services;


import com.sm.client.model.smartcar.SmResourceState;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.cache.VehiclesCache;
import com.sm.dao.AccountsDao;
import com.sm.dao.ResourcesDao;

import com.sm.model.*;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.*;

import javafx.util.Pair;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartCarService {

    private Logger logger = LoggerFactory.getLogger(SmartCarService.class);

    @Autowired
    private VehiclesCache vehiclesCache;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountsDao accountsDao;

    @Autowired
    private ResourcesDao resourcesDao;

    public void refreshCarData(String login) throws SmException, SmartcarException {
        refreshCarData(login, null);
    }

    public void refreshCarData(String login, Map<String, VehicleLocation> locationMap) throws SmException, SmartcarException {
        //need to getResources from smartCar

        SmUserSession userSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);

        if (userSession == null) {
            throw new SmException("No active smart car session found for user " + login, HttpStatus.SC_FORBIDDEN);
        }

        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(userSession.getAccountId());

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
            SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(userSession.getToken());
            for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
                Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
                String vId = vehicle.vin();
                if (vId.equals(smResource.getExternalResourceId())) {
                    return getSingleData(vehicle, vId);
                }
            }
        } catch (SmartcarException e) {
            logger.error(e.getMessage(), e);
            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
        }
        return null;
    }

    public List<SmResourceState> getResourceState(String login) throws SmException {
        SmUserSession userSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);

        if (userSession == null) {
            throw new SmException("No active smart car session found for user " + login, HttpStatus.SC_FORBIDDEN);
        }

        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(userSession.getAccountId());
        Map<String, SmResource> resourceMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a, (n, o) -> n));

        List<SmResourceState> ret = new ArrayList<>();
        Set<String> unUsedVins = new HashSet<>(resourceMap.keySet());
        try {
            SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(userSession.getToken());
            for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
                Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
                String vId = vehicle.vin();
                SmResource resource = resourceMap.get(vId);
                if (resource != null) {
                    ret.add(new SmResourceState(getSingleData(vehicle, vId), resource));
                    unUsedVins.remove(vId);
                }
            }
            for (String vId : unUsedVins) {
                SmResource resource = resourceMap.get(vId);
                ret.add(new SmResourceState(null, resource));
            }
        } catch (SmartcarException e) {
            logger.error(e.getMessage(), e);
            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
        }
        return ret;
    }

    private VehicleData getSingleData(Vehicle vehicle, String vin) {
        VehicleData vehicleData = new VehicleData();
        try {
            vehicleData.setBattery(vehicle.battery().getData());
        } catch (Exception ex) {
            logger.error("ailed to get battery info for {} due to error : {} - will use default value 50%", ex.getMessage(), ex);
            vehicleData.setBattery(new VehicleBattery(100D, 50D));
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
