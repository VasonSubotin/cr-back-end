package com.sm.client.mvc;

import com.sm.client.services.DREventService;
import com.sm.client.services.SecurityService;
import com.sm.dao.DREventsDao;
import com.sm.model.SmDREvent;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class EREventsController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DREventsDao drEventsDao;

    @Autowired
    private DREventService drEventService;

    @RequestMapping(value = "/allDREvents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmDREvent> getAllDREvents(HttpServletRequest request) throws Exception {
        return drEventsDao.getAllDREvents();
    }

    @RequestMapping(value = "/resources/{resource_id}/DREvents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmDREvent> getDREvents(HttpServletRequest request, @PathVariable("resource_id") Long resourceId) throws Exception {
        //return  drEventsDao.getDREventsByResourceId(securityService.getAccount().getIdAccount());
        return drEventService.getDREventsByResourceId(resourceId);
    }

    @RequestMapping(value = "/allDREvents", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveDREvents(HttpServletRequest request, @RequestBody List<SmDREvent> smDREvents) throws Exception {
        //return  drEventsDao.getDREventsByResourceId(securityService.getAccount().getIdAccount());
        drEventsDao.saveOrUpdateDREvents(smDREvents);
    }

    @RequestMapping(value = "/DREvent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveDREvent(HttpServletRequest request, @RequestBody SmDREvent smDREvent) throws Exception {
        //return  drEventsDao.getDREventsByResourceId(securityService.getAccount().getIdAccount());
        drEventsDao.saveOrUpdateDREvent(smDREvent);
    }
}
