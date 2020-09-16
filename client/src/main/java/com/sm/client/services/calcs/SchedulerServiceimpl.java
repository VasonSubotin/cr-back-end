package com.sm.client.services.calcs;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.GoogleService;
import com.sm.client.services.ScheduleTransformService;
import com.sm.client.services.SecurityService;
import com.sm.client.services.SmartCarService;
import com.sm.client.utils.StringDateUtil;
import com.sm.dao.ResourcesDao;
import com.sm.dao.ScheduleDao;
import com.sm.model.*;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SchedulerServiceimpl implements SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceimpl.class);

    @Autowired
    private SmartCarService smartCarService;

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
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Pair<VehicleData, SmResource> pair = getSmDataAndSmResource(resourceId);
        VehicleData smData = pair.getKey();
        SmResource smResource = pair.getValue();

        SchedulerData schedulerData = null;
        // if plugined - generates Time scheduler
        //getting current event if any
        Event event = getCurrentEvent();
        Date startTime = null;
        Date endTime = null;
        if (event == null) {
            // no calendar is avilable
            startTime = new Date();
            endTime = new Date(System.currentTimeMillis() + StringDateUtil.DAY_IN_MILLS);
            logger.info("no current event is avilable - will use unlimited time range[{} - {}]", startTime, endTime);
        } else {
            startTime = new Date(event.getStart().getDate().getValue());
            endTime = new Date(event.getEnd().getDate().getValue());
        }
        return timeScheduleService.calculateSchedule(smData, smResource, startTime, endTime);
    }

    private Pair<VehicleData, SmResource> getSmDataAndSmResource(Long resourceId) throws SmException {
        SmUserSession smUserSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);

        //trying to get current state of resources
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, smUserSession.getAccountId());
        if (smResource == null) {
            throw new SmException("Can't find resource with id=" + resourceId, 404);
        }
        // first getting current state of car
        VehicleData smData = smartCarService.getVehicleData(smUserSession, smResource);
        if (smData == null) {
            logger.error("*** Failed to get data of car for resource by external id[{}] ****", smResource.getExternalResourceId());
            throw new SmException("*** Failed to get location of car for resource by external id[" + smResource.getExternalResourceId() + "] ****", HttpStatus.SC_NOT_FOUND);
        }

        return new Pair<VehicleData, SmResource>(smData, smResource);
    }

    @Override
    public SchedulerData getLastSchdule(String login, Long resourceId) throws Exception {
        SmUserSession smUserSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);
        return scheduleTransformService.smSchedulesToScheduleWeb(scheduleDao.getLastSmSchedulesByResourceId(resourceId, smUserSession.getAccountId()));
    }

    @Override
    public List<SchedulerData> getSchduleHistory(String login, Long resourceId, Date start, Date stop) throws Exception {
        if (start == null) {
            start = new Date(0);
        }
        if (stop == null) {
            stop = new Date();
        }
        SmUserSession smUserSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);
        List<SmSchedules> schedules = scheduleDao.getLastSmSchedulesByResourceId(resourceId, smUserSession.getAccountId(), start, stop);
        List<SchedulerData> result = new ArrayList<>();
        for (SmSchedules smSchedules : schedules) {
            result.add(scheduleTransformService.smSchedulesToScheduleWeb(smSchedules));
        }
        return result;
    }

    @Override
    public SchedulerData saveSchdule(SchedulerData schedulerData, Long accountId) throws Exception {

        if (!accountId.equals(schedulerData.getAccountId())) {
            throw new SmException("You are trying to access wrong account !", HttpStatus.SC_FORBIDDEN);
        }
        SmSchedules newScheduler = scheduleTransformService.scheduleWebToSmSchedules(schedulerData);
        SmSchedules exists = scheduleDao.getLastSmSchedulesByResourceId(schedulerData.getResourceId(), accountId);
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

    private Event getCurrentEvent() throws SmException {
        try {
            Events events = googleService.getEventsForPeriodInMills(System.currentTimeMillis() - 300000, 600000);
            if (events.getItems() == null || events.getItems().isEmpty()) {
                return null;
            }
            return events.getItems().get(0);
        } catch (IOException e) {
            throw new SmException(e.getMessage(), 500);
        }
    }
}
