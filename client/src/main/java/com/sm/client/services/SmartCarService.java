package com.sm.client.services;

import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.cache.VehiclesCache;
import com.sm.dao.AccountsDao;
import com.sm.dao.ResourcesDao;
import com.sm.dao.conf.Constants;
import com.sm.model.*;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.SmartcarResponse;
import com.smartcar.sdk.data.VehicleIds;
import com.smartcar.sdk.data.VehicleInfo;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        //need to getResources from smartCar

        SmUserSession userSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);

        if (userSession == null) {
            throw new SmException("No active smart car session found for user " + login, HttpStatus.SC_FORBIDDEN);
        }

        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(userSession.getAccountId());

        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(userSession.getToken());

        refresh(resources, vehicleIdResponse, userSession);
    }

    public void refresh(List<SmResource> resources, SmartcarResponse<VehicleIds> vehicleIdResponse, SmUserSession userSession) throws SmartcarException {
        Map<String, SmResource> resourceMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a, (n, o) -> n));
        Map<String, SmResource> needToSave = new HashMap<>();

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
                needToSave.put(vId, smResource);
                resourceMap.put(vId, smResource);
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
            resourcesDao.saveResource(smResource, userSession.getAccountId());
        }
    }

    private void setBattery(SmResource smResource) {
        List<VehicleModel> lst = vehiclesCache.getModels(smResource.getVendor(), smResource.getModel());
        if (lst == null || lst.isEmpty()) {
            return;
        }
        smResource.setCapacity(lst.iterator().next().getBattery());
    }
}
