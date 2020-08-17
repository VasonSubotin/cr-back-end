package com.sm.client.mvc;

import com.sm.client.services.calcs.LocationScheduleService;
import com.sm.client.services.calcs.SchedulerService;
import com.sm.client.services.calcs.TimeScheduleService;
import com.sm.client.services.SecurityService;
import com.sm.model.PolicyType;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.SchedulerInterval;
import com.sm.client.model.to.EventInterval;
import com.sm.client.model.to.EventIntervalTO;
import com.sm.client.services.EcoService;
import com.sm.client.services.optimization.OptimizationServiceFactory;
import com.sm.client.utils.StringDateUtil;
import com.smartcar.sdk.SmartcarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.sm.client.utils.StringDateUtil.getBeginningOfDay;
import static com.sm.client.utils.StringDateUtil.getEndOfDay;

@RestController
public class BuLogicController {
    private Logger logger = LoggerFactory.getLogger(BuLogicController.class);

//    @Autowired
//    private CO2OptimizationService co2OptimizationService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private LocationScheduleService locationScheduleService;

    @Autowired
    private OptimizationServiceFactory optimizationServiceFactory;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private EcoService ecoService;

    @RequestMapping("/getSchedulerMock")
    public SchedulerData getScheduler2(HttpServletRequest request, HttpServletResponse responseR) throws SmartcarException {

        SchedulerData schedulerData = new SchedulerData();
        SchedulerInterval schedulerInterval1 = new SchedulerInterval();
        schedulerInterval1.setDuration(4505);
        schedulerInterval1.setChargeRate(5000);
        schedulerInterval1.setPrimaryTrigger("text");
        schedulerInterval1.setIntervalType(SchedulerInterval.IntervalType.CHR);
        schedulerInterval1.setCo2Impact(125);

        SchedulerInterval schedulerInterval2 = new SchedulerInterval();
        schedulerInterval2.setDuration(4506);
        schedulerInterval2.setChargeRate(4700);
        schedulerInterval2.setPrimaryTrigger("text2");
        schedulerInterval2.setIntervalType(SchedulerInterval.IntervalType.CHR);
        schedulerInterval2.setCo2Impact(121);
        schedulerData.setIntervals(Arrays.asList(schedulerInterval1, schedulerInterval1));

        return schedulerData;
    }

    @RequestMapping(value = "/getSchedulerOld", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SchedulerData getSchedulerOld(
            @RequestParam(name = "ba", required = false) String locationId,
            @RequestParam(name = "capacity", required = false, defaultValue = "30000") Long capacity,
            @RequestParam(name = "charge", required = false, defaultValue = "15000") Long charge,
            @RequestParam(name = "starttime", required = false) String starttime,
            @RequestParam(name = "endtime", required = false) String endtime,
            @RequestParam(name = "rate", required = false, defaultValue = "6600") Long rate,
            @RequestParam(name = "policy", required = false, defaultValue = "ECO") PolicyType policyType,
            @RequestParam(name = "testMode", required = false, defaultValue = "false") Boolean mock) throws Exception {

        return optimizationServiceFactory.getService(policyType).optimize(starttime, endtime, capacity, charge, rate, locationId, mock);
    }

    @RequestMapping(value = "/resources/{resourceId}/calculate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SchedulerData calculateScheduler(
            @PathVariable("resourceId") long resourceId,
            @RequestParam(name = "starttime", required = false) String starttime,
            @RequestParam(name = "endtime", required = false) String endtime) throws Exception {
        return schedulerService.calculateSchedule(resourceId, false);
    }

    @RequestMapping(value = "/resources/{resourceId}/schedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SchedulerData getScheduler(
            @PathVariable("resourceId") long resourceId) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return schedulerService.getLastSchdule(login, resourceId);
    }

    @RequestMapping(value = "/getEvents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventIntervalTO> getEvents(
            @RequestParam(name = "ba", required = false) String locationId,
            @RequestParam(name = "starttime", required = false) String starttime,
            @RequestParam(name = "endtime", required = false) String endtime,
            @RequestParam(name = "testMode", required = false, defaultValue = "false") Boolean mock
    ) throws Exception {
        if (mock) {
            List<EventIntervalTO> list = new ArrayList<>();
            Date start = StringDateUtil.parseDate(starttime);
            Date stop = StringDateUtil.parseDate(endtime);
            for (EventInterval eventInterval : ecoService.getEventIntervalMock(start, stop)) {
                list.add(new EventIntervalTO(eventInterval));
            }
            return list;
        }
        return Arrays.asList();
    }

    @RequestMapping(value = "/resources/{resource_id}/calculateGeo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SchedulerData calculateGeo(
            @PathVariable("resourceId") long resourceId,
            @RequestParam(name = "starttime", required = false) String starttime,
            @RequestParam(name = "endtime", required = false) String endtime
    ) throws Exception {
        return schedulerService.calculateSchedule(resourceId, true);
    }

//    @RequestMapping(value = "/resources/{resourceId}/calculateLocationScheduler", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public SchedulerData calculateLocationScheduler(
//            @PathVariable("resourceId") long resourceId,
//            @RequestParam(name = "starttime", required = false) String starttime,
//            @RequestParam(name = "endtime", required = false) String endtime) throws Exception {
//        Date start = StringDateUtil.parseDate(starttime);
//        start = start != null ? start : getBeginningOfDay();
//        Date stop = StringDateUtil.parseDate(endtime);
//        stop = stop != null ? stop : getEndOfDay();
//
//
//
//        return locationScheduleService.calculate(
//                securityService.getAccount().getIdAccount(),
//                resourceId,
//                300, start,
//                stop
//        );
//    }

}
