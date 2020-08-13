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
import com.sm.model.Constants;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import com.sm.model.SmUserSession;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Override
    public SchedulerData calculateSchedule(Long resourceId) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        SmUserSession smUserSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);

        //trying to get current state of resources
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, smUserSession.getAccountId());

        // first getting current state of car
        VehicleData smData = smartCarService.getVehicleData(smUserSession, smResource);
        if (smData == null) {
            logger.error("*** Failed to get data of car for resource by external id[{}] ****", smResource.getExternalResourceId());
            throw new SmException("*** Failed to get location of car for resource by external id[" + smResource.getExternalResourceId() + "] ****", HttpStatus.SC_NOT_FOUND);
        }
        SchedulerData schedulerData = null;
        //checking state of recource
        if (smData.getCharge() != null && smData.getCharge().getData() != null && smData.getCharge().getData().getIsPluggedIn()) {
            // if plugined - generates Time scheduler
            //getting current event if any
            Event event = getCurrentEvent();
            String startTime = null;
            String endTime = null;
            if (event == null) {
                // no calendar is avilable
                startTime = sdf.format(new Date());
                endTime = sdf.format(new Date(System.currentTimeMillis() + StringDateUtil.DAY_IN_MILLS));
                logger.info("no current event is avilable - will use unlimited time range[{} - {}]", startTime, endTime);
            } else {
                startTime = sdf.format(new Date(event.getStart().getDate().getValue()));
                endTime = sdf.format(new Date(event.getEnd().getDate().getValue()));
            }
            schedulerData = timeScheduleService.calculateSchedule(smData, smResource, startTime, endTime);
        } else {
            //generates location scheduler
            schedulerData = locationScheduleService.calculate(smUserSession.getAccountId(), smData, smResource);
        }
        schedulerData.setInitialEnergy((long) (smData.getBattery().getPercentRemaining() * (double) smResource.getCapacity()));
        return schedulerData;
    }

    @Override
    public SchedulerData getLastSchdule(String login, Long resourceId) throws Exception {
        SmUserSession smUserSession = securityService.getActiveSessionByLogin(Constants.SMART_CAR_AUTH_TYPE, login);
        return scheduleTransformService.smSchedulesToScheduleWeb(scheduleDao.getLastSmSchedulesByResourceId(resourceId, smUserSession.getAccountId()));
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
