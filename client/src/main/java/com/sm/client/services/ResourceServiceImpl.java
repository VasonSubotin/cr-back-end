package com.sm.client.services;

import com.sm.client.model.smartcar.SmResourceState;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.cache.SmartCarCacheService;
import com.sm.client.services.cache.VehiclesCache;
import com.sm.dao.ResourcesDao;
import com.sm.dao.UserSessionDao;
import com.sm.model.*;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.SmartcarResponse;
import com.smartcar.sdk.data.VehicleIds;
import com.smartcar.sdk.data.VehicleInfo;
import com.smartcar.sdk.data.VehicleLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);


    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SmartCarService smartCarService;

    @Autowired
    private SmartCarCacheService smartCarCacheService;

    @Autowired
    private UserSessionDao userSessionDao;

    @Autowired
    private VehiclesCache vehiclesCache;

    @Override
    public SmResourceState getResourceState(Long resourceId) throws SmException {
        return smartCarCacheService.getResourceState(resourceId);
    }

//    @Override
//    public SmResourceState getResourceState(Long resourceId) throws SmException {
//
//        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, securityService.getAccount().getIdAccount());
//
//        SmResourceState smResourceState = new SmResourceState();
//        if (smResource != null && smResource.getExternalResourceId() != null) {
//            SmartCarCache smartCarCache = commonService.getSmartCarCache(smResource.getExternalResourceId());
//            smResourceState.setSmartCarInfo(smartCarService.createVehicleDataFromSmartCarCache(smartCarCache));
//            if (smartCarCache != null) {
//                smResourceState.setTimers(Arrays.asList(new SmTiming("smartcar request", smartCarCache.getTiming())));
//            } else {
//                smResourceState.setSmartCarInfo(new VehicleData());
//            }
//        }
//
//        smResourceState.setSmResource(smResource);
//        return smResourceState;
//    }

    public List<SmResourceState> getResourceStates() throws SmException {
        return smartCarCacheService.getResourcesStatesByAccount();
    }
//
//    @Override
//    public List<SmResourceState> getResourceStates() throws SmException {
//        List<SmResource> smResources = resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount());
//        if (smResources == null || smResources.isEmpty()) {
//            return new ArrayList<>();
//        }
//        List<SmResourceState> smResourceStates = new ArrayList<>();
//        Set<String> vins = smResources.stream().map(SmResource::getExternalResourceId).collect(Collectors.toSet());
//        List<SmartCarCache> smartCarCacheList = commonService.getSmartCarCacheIn(vins);
//        if (smartCarCacheList != null && !smartCarCacheList.isEmpty()) {
//            Map<String, SmartCarCache> smartCarCacheMap = smartCarCacheList.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a));
//            for (SmResource smResource : smResources) {
//                smResource.setImagePath(getResourceImage(smResource.getIdResource()));
//                SmResourceState smResourceState = new SmResourceState(
//                        smartCarService.createVehicleDataFromSmartCarCache(smartCarCacheMap.get(smResource.getExternalResourceId())),
//                        smResource);
//                smResourceStates.add(smResourceState);
//            }
//        } else {
//            for (SmResource smResource : smResources) {
//                smResource.setImagePath(getResourceImage(smResource.getIdResource()));
//                smResourceStates.add(new SmResourceState(new VehicleData(), smResource));
//            }
//        }
//
//        return smResourceStates;
//    }

    @Override
    public String getResourceImage(Long resourceId) throws SmException {
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, securityService.getAccount().getIdAccount());
        if (smResource == null) {
            return null;
        }

        return resourcesDao.getImageByResource(smResource);
    }


    @Override
    public void refreshCarData(SmUserSession smUserSession) throws SmException, SmartcarException {
        refreshCarData(smUserSession, null);
    }

    public void refreshCarData(SmUserSession userSession, Map<String, VehicleLocation> locationMap) throws SmException, SmartcarException {
        //need to getResources from smartCar
        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount());

        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(userSession.getToken());

        addResources(resources, vehicleIdResponse, userSession, locationMap);
    }

    public void addResources(List<SmResource> resources, SmartcarResponse<VehicleIds> vehicleIdResponse, SmUserSession userSession, Map<String, VehicleLocation> locationMap) throws SmartcarException, SmException {
        Map<String, SmResource> resourceMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a, (n, o) -> n));
        Map<String, SmResource> needToSave = new HashMap<>();
        List<SmTiming> timmers = new ArrayList<>();
        for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
            Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
            String vId = vehicle.vin();

            VehicleData vehicleData = smartCarService.getSingleDataBatch(vehicle, vId, timmers);
            smartCarCacheService.saveSmartCarCache(vehicleData, timmers.isEmpty() ? 0l : timmers.get(timmers.size() - 1).getTime());

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
                    locationMap.put(smResource.getExternalResourceId(), vehicleData.getLocation().getData());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }

            try {
                VehicleInfo vehicleInfo = vehicleData.getVehicleInfo();

                smResource.setModel(vehicleInfo.getModel());
                smResource.setVendor(vehicleInfo.getMake());
                needToSave.put(vId, smResource);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

//            try {
//                vehicleData.getCharge();
//            } catch (Exception ex) {
//                logger.error(ex.getMessage(), ex);
//            }
        }

        for (SmResource smResource : needToSave.values()) {
            smResource.setDtUpdated(new Date());
            setBattery(smResource);
            if (smResource.getIdResource() == null) {
                resourcesDao.saveResource(smResource, userSession.getAccountId());
            }else {
                resourcesDao.updateResource(smResource, userSession.getAccountId());
            }

            SmUserSession userSessionClone = securityService.createSmUserSession(userSession.getSessionType(), userSession.getToken(), userSession.getRefreshToken(), userSession.getTtl(), userSession.getAccountId());
            userSessionClone.setResourceId(smResource.getIdResource());
            userSessionDao.deleteSession(smResource.getAccountId(), smResource.getIdResource(), userSession.getSessionType());
            userSessionDao.saveSession(userSessionClone);
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
