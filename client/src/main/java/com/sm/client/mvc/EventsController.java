package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.EventsDao;
import com.sm.dao.SessionsDao;
import com.sm.model.SmEvent;
import com.sm.model.SmSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class EventsController {
    @Autowired
    private SecurityService securityService;

    @Autowired
    private EventsDao eventsDao;

    //2016-01-01T05:00:00-05:00
    @RequestMapping(value = "/events/{resource_id}/events?start_date={start}&end_date={stop}&limit={limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmEvent> getUserEventsById(HttpServletRequest request,
                                           @PathVariable("resource_id") long resourceId,
                                           @RequestParam(value = "start", required = false)
                                           @DateTimeFormat(pattern = "yyyy-dd-MM'T'HH:mm:ssZ") Date start,
                                           @RequestParam(value = "stop", required = false)
                                           @DateTimeFormat(pattern = "yyyy-dd-MM'T'HH:mm:ssZ") Date stop,
                                           @RequestParam(value = "limit", required = false, defaultValue = "100") long limit
    ) throws Exception {
        Long accountId = securityService.getAccount().getIdAccount();
        if (start != null || stop != null) {
            start = start == null ? new Date(0) : start;
            stop = stop == null ? new Date(System.currentTimeMillis() + 100000000) : stop;
            return eventsDao.getAllEventsByAccountIdAndResourceIdAndTimeRange(accountId, resourceId, start, stop, limit);
        }
        return Arrays.asList(eventsDao.getLastEventByAccountIdAndResourceId(accountId, resourceId));
    }

}
