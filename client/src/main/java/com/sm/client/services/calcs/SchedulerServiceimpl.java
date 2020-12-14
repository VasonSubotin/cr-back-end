package com.sm.client.services.calcs;

import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.GoogleService;
import com.sm.client.services.ScheduleTransformService;
import com.sm.client.services.SecurityService;
import com.sm.client.services.SmartCarService;
import com.sm.client.services.cache.SmartCarCacheService;
import com.sm.client.utils.StringDateUtil;
import com.sm.dao.ResourcesDao;
import com.sm.dao.ScheduleDao;
import com.sm.model.*;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SchedulerServiceimpl implements SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceimpl.class);

//    @Autowired
//    private SmartCarService smartCarService;

    @Autowired
    private SmartCarCacheService smartCarCacheService;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TimeScheduleService timeScheduleService;

    @Autowired
    private LocationScheduleService locationScheduleService;

    @Autowired
    private GoogleService googleService;


    @Autowired
    private ScheduleTransformService scheduleTransformService;

    @Autowired
    private ScheduleDao scheduleDao;

//    @Override
//    public SchedulerData calculateSchedule(Long resourceId, boolean geo) throws Exception {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//
//        SmUserSession smUserSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);
//
//        //trying to get current state of resources
//        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, smUserSession.getAccountId());
//        if (smResource == null) {
//            throw new SmException("Can't find resource with id=" + resourceId, 404);
//        }
//        // first getting current state of car
//        VehicleData smData = smartCarService.getVehicleData(smUserSession, smResource);
//        if (smData == null) {
//            logger.error("*** Failed to get data of car for resource by external id[{}] ****", smResource.getExternalResourceId());
//            throw new SmException("*** Failed to get location of car for resource by external id[" + smResource.getExternalResourceId() + "] ****", HttpStatus.SC_NOT_FOUND);
//        }
//        SchedulerData schedulerData = null;
//        //checking state of recource
//        if (geo) {
//            schedulerData = locationScheduleService.calculateGeo(smUserSession.getAccountId(), smData, smResource);
//        } else {
//            if (smData.getCharge() != null && smData.getCharge().getData() != null && smData.getCharge().getData().getIsPluggedIn()) {
//                // if plugined - generates Time scheduler
//                //getting current event if any
//                Event event = getCurrentEvent();
//                String startTime = null;
//                String endTime = null;
//                if (event == null) {
//                    // no calendar is avilable
//                    startTime = sdf.format(new Date());
//                    endTime = sdf.format(new Date(System.currentTimeMillis() + StringDateUtil.DAY_IN_MILLS));
//                    logger.info("no current event is avilable - will use unlimited time range[{} - {}]", startTime, endTime);
//                } else {
//                    startTime = sdf.format(new Date(event.getStart().getDate().getValue()));
//                    endTime = sdf.format(new Date(event.getEnd().getDate().getValue()));
//                }
//                schedulerData = timeScheduleService.calculateSchedule(smData, smResource, startTime, endTime);
//            } else {
//                //generates location scheduler
//                schedulerData = locationScheduleService.calculate(smUserSession.getAccountId(), smData, smResource);
//            }
//        }
//        schedulerData.setInitialEnergy((long) (smData.getBattery().getPercentRemaining() * (double) smResource.getCapacity()));
//        scheduleDao.saveSmSchedules(scheduleTransformService.scheduleWebToSmSchedules(schedulerData));
//        return schedulerData;
//    }

    @Override
    public SchedulerData calculateDrivingScheduleGeo(Long resourceId) throws Exception {

        Pair<VehicleData, SmResource> pair = getSmDataAndSmResource(resourceId);
        VehicleData smData = pair.getKey();
        SmResource smResource = pair.getValue();

        //generates location scheduler
        return locationScheduleService.calculateGeo(smResource.getAccountId(), smData, smResource);
    }

    @Override
    public SchedulerData calculateDrivingSchedule(Long resourceId) throws Exception {

        Pair<VehicleData, SmResource> pair = getSmDataAndSmResource(resourceId);
        VehicleData smData = pair.getKey();
        SmResource smResource = pair.getValue();

        //generates location scheduler
        return locationScheduleService.calculate(smResource.getAccountId(), smData, smResource);
    }

    @Override
    public SchedulerData calculateCharingSchedule(Long resourceId) throws Exception {

        Pair<VehicleData, SmResource> pair = getSmDataAndSmResource(resourceId);
        VehicleData smData = pair.getKey();
        SmResource smResource = pair.getValue();

        Date startTime = new Date();
        Date endTime = null;

        if (smResource.getnChargeByTime() != null) {
            endTime = StringDateUtil.setTimeFromMinutesOfDay(startTime, smResource.getnChargeByTime());
            if (endTime.before(startTime)) {
                endTime = new Date(endTime.getTime() + StringDateUtil.DAY_IN_MILLS);
            }
        } else {
            endTime = new Date(System.currentTimeMillis() + StringDateUtil.DAY_IN_MILLS);
        }
        logger.info("no current event is avilable - will use unlimited time range[{} - {}]", startTime, endTime);
        return timeScheduleService.calculateSchedule(smData, smResource, startTime, endTime);
    }

    private Pair<VehicleData, SmResource> getSmDataAndSmResource(Long resourceId) throws SmException {

        //trying to get current state of resources
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, securityService.getAccount().getIdAccount());
        if (smResource == null) {
            throw new SmException("Can't find resource with id=" + resourceId, 404);
        }
        // first getting current state of car
        VehicleData smData = smartCarCacheService.getVehicleData(smResource.getExternalResourceId());
        if (smData == null) {
            logger.error("*** Failed to get data of car for resource by external id[{}] ****", smResource.getExternalResourceId());
            throw new SmException("*** Failed to get location of car for resource by external id[" + smResource.getExternalResourceId() + "] ****", HttpStatus.SC_NOT_FOUND);
        }

        return new Pair<VehicleData, SmResource>(smData, smResource);
    }

    @Override
    public SchedulerData getLastSchdule(String login, Long resourceId, SmScheduleType type) throws Exception {
        SmUserSession smUserSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login, resourceId);
        return scheduleTransformService.smSchedulesToScheduleWeb(scheduleDao.getLastSmSchedulesByResourceIdAndType(resourceId, smUserSession.getAccountId(), type));
    }

    @Override
    public List<SchedulerData> getSchduleHistory(String login, Long resourceId, Date start, Date stop, SmScheduleType type) throws Exception {
        if (start == null) {
            start = new Date(0);
        }
        if (stop == null) {
            stop = new Date();
        }
        Long accountId = securityService.getAccount().getIdAccount();
        //SmUserSession smUserSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login, resourceId);
        List<SmSchedules> schedules = scheduleDao.getNoIntervalSmSchedulesByResourceIdAndDateBetween(resourceId, accountId, start, stop, type);
        Map<Long, SmResource> resourceMap = resourcesDao.getAllResourceByAccountId(accountId).stream().collect(Collectors.toMap(SmResource::getIdResource, a -> a));
        List<SchedulerData> result = new ArrayList<>();
        for (SmSchedules smSchedules : schedules) {
            SchedulerData schedulerData = scheduleTransformService.smSchedulesToScheduleWeb(smSchedules);
            SmResource smResource = resourceMap.get(smSchedules.getResourceId());
            if(smResource!=null){
                schedulerData.setModel(smResource.getModel());
                schedulerData.setVendor(smResource.getVendor());
            }
            result.add(schedulerData);
        }
        return result;
    }

    @Override
    public SchedulerData saveSchdule(SchedulerData schedulerData, Long accountId) throws Exception {

        if (!accountId.equals(schedulerData.getAccountId())) {
            throw new SmException("You are trying to access wrong account !", HttpStatus.SC_FORBIDDEN);
        }
        SmSchedules newScheduler = scheduleTransformService.scheduleWebToSmSchedules(schedulerData);
        SmSchedules exists = scheduleDao.getSmSchedulesById(schedulerData.getSchedulerId(), accountId);
        if (exists == null) {
            return scheduleTransformService.smSchedulesToScheduleWeb(scheduleDao.saveSmSchedules(newScheduler));
        }

        //overwriting all
        exists.setCarbonSavings(newScheduler.getCarbonSavings());
        exists.setCarbonImpact(newScheduler.getCarbonImpact());
        exists.setLocationId(newScheduler.getLocationId());
        exists.setAccountId(newScheduler.getAccountId());
        exists.setPolicyId(newScheduler.getPolicyId());
        exists.setFinanceSavings(newScheduler.getFinanceSavings());
        exists.setResourceId(newScheduler.getResourceId());
        exists.setSessionId(newScheduler.getSessionId());
        exists.setDtStart(newScheduler.getDtStart());
        exists.setDtStop(newScheduler.getDtStop());
        exists.setIdSchedule(newScheduler.getIdSchedule());
        exists.setDtCreated(newScheduler.getDtCreated());
        exists.setInitEnergy(newScheduler.getInitEnergy());
        exists.setData(newScheduler.getData());

        return scheduleTransformService.smSchedulesToScheduleWeb(scheduleDao.saveSmSchedules(exists));
    }

//    private Event getCurrentEvent() throws SmException {
//        try {
//            Events events = googleService.getEventsForPeriodInMills(System.currentTimeMillis() - 300000, 600000);
//            if (events.getItems() == null || events.getItems().isEmpty()) {
//                return null;
//            }
//            return events.getItems().get(0);
//        } catch (IOException e) {
//            throw new SmException(e.getMessage(), 500);
//        }
//    }
}
