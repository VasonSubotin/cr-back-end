package com.sm.client.services;

import com.sm.client.model.smartcar.VehicleData;
import com.sm.dao.ResourcesDao;
import com.sm.dao.conf.Constants;
import com.sm.model.SmAccount;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import com.sm.model.SmUserSession;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmartCarService {

    private Logger logger = LoggerFactory.getLogger(SmartCarService.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ResourcesDao resourcesDao;

    public void refreshCarData() throws SmException, SmartcarException {
        //need to getResources from smartCar
        SmUserSession userSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);
        SmAccount smAccount = securityService.getAccount();
        if (userSession == null) {
            throw new SmException("No active smart car session found for user " + smAccount.getLogin(), HttpStatus.SC_FORBIDDEN);
        }

        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(smAccount.getIdAccount());
        Map<String, SmResource> resourceMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a));

        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(userSession.getToken());

        Map<String, SmResource> needToSave = new HashMap<>();

        for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {

            SmResource smResource = resourceMap.get(vehicleId);
            if (smResource == null) {
                smResource = new SmResource();
                smResource.setExternalResourceId(vehicleId);
                smResource.setAccountId(smAccount.getIdAccount());
                needToSave.put(vehicleId, smResource);
                resourceMap.put(vehicleId, smResource);
            }


            Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());

            try {
                VehicleInfo vehicleInfo = vehicle.info();
                smResource.setModel(vehicleInfo.getModel());
                smResource.setVendor(vehicleInfo.getMake());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicle.charge();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

        }
    }
}
