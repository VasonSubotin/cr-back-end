package com.sm.client.services.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.sm.client.model.smartcar.SmResourceState;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.SecurityService;
import com.sm.client.services.SmartCarService;
import com.sm.dao.ResourcesDao;
import com.sm.dao.cache.SmartCarCacheDao;
import com.sm.model.*;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.SmartcarResponse;
import com.smartcar.sdk.data.VehicleIds;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Scope("singleton")
public class SmartCarCacheService {
    private static final Logger logger = LoggerFactory.getLogger(SmartCarCacheService.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SmartCarService smartCarService;

    @Autowired
    private SmartCarCacheDao smartCarCacheDao;

    @Autowired
    private ResourcesDao resourcesDao;

    @Qualifier("commonThreadPool")
    @Autowired
    private TaskExecutor commonTaskExecutor;

    private Map<String, Date> needUpdateCache = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    public void refreshOnBackground(String login) {
        commonTaskExecutor.execute(() -> {
            try {
                refreshSmartCarCacheByLogin(login);
            } catch (SmException e) {
                logger.error("Failed to update cache for login " + login + ": " + e.getMessage(), e);
            }
        });
    }

    public void refreshSmartCarCacheByLogin(String login) throws SmException {
        try {
            List<SmTiming> timers = new ArrayList();
            List<SmUserSession> userSessions = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);
            //for each session doing refresh
            for (SmUserSession userSession : userSessions) {
                Pair<SmUserSession, SmartcarResponse<VehicleIds>> p = smartCarService.getVehicleIds(userSession);
                userSession = p.getKey();
                if (userSession == null) {
                    //if no session we just ignore it with log, but if there is session and we failed to get data then we throw exception
                    logger.error("No active smart car session found for login " + SecurityContextHolder.getContext().getAuthentication().getName());
                    continue;
                }

                SmartcarResponse<VehicleIds> vehicleIdResponse = p.getValue();
                for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
                    Vehicle vehicle = new Vehicle(vehicleId, userSession.getToken());
                    String vId = vehicle.vin();
                    VehicleData vehicleData = smartCarService.getSingleDataBatch(vehicle, vId, timers);
                    saveSmartCarCache(vehicleData, timers.isEmpty() ? null : timers.get(timers.size() - 1).getTime());
                }
            }

        } catch (SmartcarException e) {
            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
        }
    }

    public List<SmResourceState> getResourcesStatesByAccount() throws SmException {
        Long accountId = securityService.getAccount().getIdAccount();
        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(accountId);
        Set<String> vins = resources.stream().map(SmResource::getExternalResourceId).collect(Collectors.toSet());
        List<SmartCarCache> smartCarCacheList = smartCarCacheDao.getSmartCarCacheIn(vins);
        Map<String, SmartCarCache> smartCarCacheMap = smartCarCacheList.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a));
        List<SmResourceState> ret = new ArrayList<>();
        for (SmResource resource : resources) {
            VehicleData vehicleData = createVehicleDataFromSmartCarCache(smartCarCacheMap.get(resource.getExternalResourceId()));
            ret.add(new SmResourceState(vehicleData, resource));
        }
        return ret;
    }

    public VehicleData getVehicleData(String externalResourceId) {
        SmartCarCache smartCarCache = smartCarCacheDao.getSmartCarCache(externalResourceId);
        return createVehicleDataFromSmartCarCache(smartCarCache);
    }

    public SmResourceState getResourceState(Long resourceId) throws SmException {
        try {
            Long accountId = securityService.getAccount().getIdAccount();
            SmResource resource = resourcesDao.getResourceByIdAndAccountId(resourceId, accountId);
            VehicleData vehicleData = createVehicleDataFromSmartCarCache(smartCarCacheDao.getSmartCarCache(resource.getExternalResourceId()));
            return new SmResourceState(vehicleData, resource);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
        }
    }


    public void saveSmartCarCache(VehicleData vehicleData, long timing) {
        try {
            SmartCarCache smartCarCache = smartCarCacheDao.getSmartCarCache(vehicleData.getVin());
            if (smartCarCache != null) {
                smartCarCache.setDtCreated(new Date());
                smartCarCache.setExternalResourceId(vehicleData.getVin());
                smartCarCache.setData(objectMapper.writeValueAsBytes(vehicleData));
                smartCarCache.setTiming(timing);
            } else {
                smartCarCache = createSmartCarCacheFromVehicleData(vehicleData, timing);
            }

            smartCarCacheDao.saveSmartCarCache(smartCarCache);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    public SmartCarCache createSmartCarCacheFromVehicleData(VehicleData vehicleData, long timing) throws
            JsonProcessingException {
        SmartCarCache smartCarCache = new SmartCarCache();
        smartCarCache.setDtCreated(new Date());
        smartCarCache.setExternalResourceId(vehicleData.getVin());
        smartCarCache.setData(objectMapper.writeValueAsBytes(vehicleData));
        smartCarCache.setTiming(timing);
        return smartCarCache;
    }

    public VehicleData createVehicleDataFromSmartCarCache(SmartCarCache smartCarCache) {
        if (smartCarCache == null) {
            return null;
        }
        try {
            return objectMapper.readValue(smartCarCache.getData(), new TypeReference<VehicleData>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public List<SmResourceState> getRealTimeResourceState(String login) throws SmException {
        List<SmTiming> timers = new ArrayList<>();
        long start = System.currentTimeMillis();
        Collection<SmUserSession> userSessions = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);
        timers.add(new SmTiming("getActiveSessionByLogin", System.currentTimeMillis() - start));

        if (userSessions == null || userSessions.isEmpty()) {
            throw new SmException("No active smart car session found for user " + login, HttpStatus.SC_FORBIDDEN);
        }
        Long accountId = userSessions.iterator().next().getAccountId();
        List<SmResourceState> ret = new ArrayList<>();
        start = System.currentTimeMillis();
        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(accountId);
        timers.add(new SmTiming("getAllResourceByAccountId", System.currentTimeMillis() - start));
        Map<String, SmResource> resourceMap = resources.stream().collect(Collectors.toMap(a -> a.getExternalResourceId(), a -> a, (n, o) -> n));


        Set<String> unUsedVins = new HashSet<>(resourceMap.keySet());
        for (SmUserSession userSession : userSessions) {
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
                        VehicleData vehicleData = smartCarService.getSingleData(vehicle, vId, timers);
                        ret.add(new SmResourceState(vehicleData, resource));
                        saveSmartCarCache(vehicleData, timers.isEmpty() ? null : timers.get(timers.size() - 1).getTime());
                        unUsedVins.remove(vId);
                    }
                    timers.add(new SmTiming("resourceMap.get  " + vId, System.currentTimeMillis() - start));
                }
            } catch (SmartcarException e) {
                logger.error(e.getMessage(), e);
                throw new SmException(e.getMessage(), HttpStatus.SC_EXPECTATION_FAILED);
            }
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

        return ret;
    }

    public void addLoginForUpdate(String login) {
        needUpdateCache.put(login, new Date());
        //need to check status of thread
    }

    @Scheduled(fixedDelayString = "${interval.check.delay.seconds:60}000")
    private void updateCache() {
        Set<String> logins = new HashSet<>();

        //to prevent locking, doing copy
        logins.addAll(needUpdateCache.keySet());

        long current = System.currentTimeMillis();
        for (String login : logins) {
            Date date = needUpdateCache.get(login);
            if (((current - date.getTime()) / 60_000) % 5 == 0) {
                refreshOnBackground(login);
            }
            if (current - date.getTime() > 1800_000) {
                needUpdateCache.remove(login);
            }
        }
    }

}
