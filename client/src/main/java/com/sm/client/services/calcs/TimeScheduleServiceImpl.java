package com.sm.client.services.calcs;

import com.sm.client.model.eco.GridData;
import com.sm.client.model.eco.GridDataAggregated;
import com.sm.client.model.eco.LocationData;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.SchedulerInterval;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.EcoService;
import com.sm.client.services.ScheduleTransformService;
import com.sm.client.services.SecurityService;
import com.sm.client.services.optimization.OptimizationServiceFactory;
import com.sm.dao.ScheduleDao;
import com.sm.model.*;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TimeScheduleServiceImpl implements TimeScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(TimeScheduleServiceImpl.class);

    private static final long DEFAULT_RATE = 10000;

    @Autowired
    private OptimizationServiceFactory optimizationServiceFactory;

    @Autowired
    private EcoService ecoService;


    @Autowired
    private SecurityService securityService;

    @Autowired
    private ScheduleTransformService scheduleTransformService;

    @Autowired
    private ScheduleDao scheduleDao;

    @Override
    public SchedulerData calculateSchedule(VehicleData smData, SmResource smResource, Date starttime, Date endtime) throws Exception {

        PolicyType policyType = PolicyType.getById(smResource.getPolicyId());
        if (policyType == null) {
            logger.error("*** Failed to understand policy by id[{}] ****", smResource.getPolicyId());
            throw new SmException("Failed to understand policy by id[" + smResource.getPolicyId() + "]", HttpStatus.SC_NOT_FOUND);
        }

        String locationAbrv = "ba";

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
                (long) (smResource.getCapacity().doubleValue() * smData.getBattery().getPercentRemaining()),
                rate,
                locationAbrv,
                smResource.getIdResource());
        schedulerData.setAccountId(smResource.getAccountId());
        schedulerData.setResourceId(smResource.getIdResource());
        schedulerData.setPolicyId(smResource.getPolicyId());
        schedulerData.setCreatedTime(new Date());
        schedulerData.setScheduleType(SmScheduleType.CHR);
        schedulerData.setCapacity(smResource.getCapacity());
        GridDataAggregated grid = schedulerData.getMoers();
        setEndSoc(schedulerData);
        schedulerData = scheduleTransformService.smSchedulesToScheduleWeb(
                scheduleDao.saveSmSchedules(
                        scheduleTransformService.scheduleWebToSmSchedules(schedulerData)));
        schedulerData.setMoers(grid);
        return schedulerData;
    }

    protected void setEndSoc(SchedulerData schedulerData) {
        long energy = schedulerData.getInitialEnergy();
        for (SchedulerInterval interval : schedulerData.getIntervals()) {
            energy += interval.getEnergy();
        }
        schedulerData.setEndSoc(100.0 * energy / (double) schedulerData.getCapacity());
    }

    public void setOptimizationServiceFactory(OptimizationServiceFactory optimizationServiceFactory) {
        this.optimizationServiceFactory = optimizationServiceFactory;
    }

    public void setEcoService(EcoService ecoService) {
        this.ecoService = ecoService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setScheduleTransformService(ScheduleTransformService scheduleTransformService) {
        this.scheduleTransformService = scheduleTransformService;
    }

    public void setScheduleDao(ScheduleDao scheduleDao) {
        this.scheduleDao = scheduleDao;
    }
}
