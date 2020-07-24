package com.sm.client.services;

import com.sm.client.model.eco.LocationData;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.optimization.OptimizationServiceFactory;
import com.sm.dao.ResourcesDao;
import com.sm.dao.ScheduleDao;
import com.sm.dao.conf.Constants;
import com.sm.model.PolicyType;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import com.sm.model.SmUserSession;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private static final long DEFAULT_RATE = 10000;
    @Autowired
    private OptimizationServiceFactory optimizationServiceFactory;

    @Autowired
    private EcoService ecoService;

    @Autowired
    private SmartCarService smartCarService;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ScheduleTransformService scheduleTransformService;

    @Autowired
    private ScheduleDao scheduleDao;

    @Override
    public SchedulerData calculateSchedule(String login, Long resourceId, String starttime, String endtime) throws Exception {

        SmUserSession smUserSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);

        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, smUserSession.getAccountId());

        PolicyType policyType = PolicyType.getById(smResource.getPolicyId());
        if (policyType == null) {
            logger.error("*** Failed to understand policy by id[{}] ****", smResource.getPolicyId());
            throw new SmException("Failed to understand policy by id[" + smResource.getPolicyId() + "]", HttpStatus.SC_NOT_FOUND);
        }

        logger.debug("resource[{}] - found location policyType[{}]", smResource.getIdResource(), policyType);

        // first getting current state of car
        VehicleData smData = smartCarService.getVehicleData(smUserSession, smResource);

        String locationAbrv = "ba";
        if (smData == null) {
            logger.error("*** Failed to get data of car for resource by external id[{}] ****", smResource.getExternalResourceId());
            throw new SmException("*** Failed to get location of car for resource by external id[" + smResource.getExternalResourceId() + "] ****", HttpStatus.SC_NOT_FOUND);
        }

        if (smData.getLocation() == null || smData.getLocation().getData() == null) {
            logger.error("*** Failed to get location of car for resource by external id[{}] -- will use default location ba ****", smResource.getExternalResourceId());
        } else {
            LocationData locationData = ecoService.getLocation(smData.getLocation().getData().getLatitude(), smData.getLocation().getData().getLongitude());
            locationAbrv = locationData.getAbbrev();
        }
        logger.debug("resource[{}] - found location abbrev[{}]", smResource.getIdResource(), locationAbrv);
        long rate = smResource.getPower() == null ? DEFAULT_RATE : smResource.getPower();
        logger.debug("resource[{}] - using  rate[{}]", smResource.getIdResource(), rate);
        SchedulerData schedulerData = optimizationServiceFactory.getService(policyType).optimize(
                starttime,
                endtime,
                smResource.getCapacity(),
                (long) (smResource.getCapacity().doubleValue() * (100D - smData.getBattery().getPercentRemaining()) / 100D),
                rate,
                locationAbrv,
                false);
        schedulerData.setAccountId(smResource.getAccountId());
        schedulerData.setResourceId(smResource.getIdResource());
        schedulerData.setPolicyId(smResource.getPolicyId());

        return scheduleTransformService.smSchedulesToScheduleWeb(scheduleDao.saveSmSchedules(scheduleTransformService.scheduleWebToSmSchedules(schedulerData)));
    }


    @Override
    public SchedulerData getLastSchdule(String login, Long resourceId) throws Exception {
        SmUserSession smUserSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);
        return scheduleTransformService.smSchedulesToScheduleWeb(scheduleDao.getLastSmSchedulesByResourceId(resourceId, smUserSession.getAccountId()));
    }

}
