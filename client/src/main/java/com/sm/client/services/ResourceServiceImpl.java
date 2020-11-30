package com.sm.client.services;

import com.sm.client.model.smartcar.SmResourceState;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.dao.ResourcesDao;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import com.sm.model.SmTiming;
import com.sm.model.SmartCarCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Value("${smartcar.resources.imagesPath:/}")
    private String pathPrefix = "/";

    @Value("${smartcar.resources.imagesExt:.jpg}")
    private String imagesExt = ".jpg";

    @Value("${smartcar.resources.imagesUrl:}")
    private String imagesUrl = "";

    @Value("${smartcar.resources.imagesGeneral:general}")
    private String imagesGeneral = "general";

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SmartCarService smartCarService;

    @Override
    public SmResourceState getResourceState(Long resourceId) throws SmException {
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, securityService.getAccount().getIdAccount());

        SmResourceState smResourceState = new SmResourceState();
        if (smResource != null && smResource.getExternalResourceId() != null) {
            SmartCarCache smartCarCache = commonService.getSmartCarCache(smResource.getExternalResourceId());
            smResourceState.setSmartCarInfo(smartCarService.createVehicleDataFromSmartCarCache(smartCarCache));
            if (smartCarCache != null) {
                smResourceState.setTimers(Arrays.asList(new SmTiming("smartcar request", smartCarCache.getTiming())));
            } else {
                smResourceState.setSmartCarInfo(new VehicleData());
            }
        }

        smResourceState.setSmResource(smResource);
        return smResourceState;
    }

    @Override
    public List<SmResourceState> getResourceStates() throws SmException {
        List<SmResource> smResources = resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount());
        if (smResources == null || smResources.isEmpty()) {
            return new ArrayList<>();
        }
        List<SmResourceState> smResourceStates = new ArrayList<>();
        Set<String> vins = smResources.stream().map(SmResource::getExternalResourceId).collect(Collectors.toSet());
        List<SmartCarCache> smartCarCacheList = commonService.getSmartCarCacheIn(vins);
        if (smartCarCacheList != null && !smartCarCacheList.isEmpty()) {
            Map<String, SmartCarCache> smartCarCacheMap = smartCarCacheList.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a));
            for (SmResource smResource : smResources) {
                smResource.setImagePath(getResourceImage(smResource.getIdResource()));
                SmResourceState smResourceState = new SmResourceState(
                        smartCarService.createVehicleDataFromSmartCarCache(smartCarCacheMap.get(smResource.getExternalResourceId())),
                        smResource);
                smResourceStates.add(smResourceState);
            }
        } else {
            for (SmResource smResource : smResources) {
                smResource.setImagePath(getResourceImage(smResource.getIdResource()));
                smResourceStates.add(new SmResourceState(new VehicleData(), smResource));
            }
        }

        return smResourceStates;
    }

    @Override
    public String getResourceImage(Long resourceId) throws SmException {
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, securityService.getAccount().getIdAccount());
        if (smResource == null) {
            return null;
        }

        if (!new File(pathPrefix + "/" + smResource.getVendor() + "/" + smResource.getModel() + imagesExt).exists()) {
            // looking for vendor
            if (!new File(pathPrefix + "/" + smResource.getVendor() + "/" + imagesGeneral + imagesExt).exists()) {
                return imagesUrl + imagesGeneral + imagesExt;
            }
            return imagesUrl  + smResource.getVendor() + "/" + imagesGeneral + imagesExt;
        }
        return imagesUrl  + smResource.getVendor() + "/" + smResource.getModel() + imagesExt;
    }
}
