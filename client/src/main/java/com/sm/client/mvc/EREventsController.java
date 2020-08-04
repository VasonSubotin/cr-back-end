package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.DREventsDao;
import com.sm.model.SmDREvent;
import com.sm.model.SmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class EREventsController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DREventsDao drEventsDao;

    @RequestMapping(value = "/allDREvents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  List<SmDREvent> getAllDREvents(HttpServletRequest request) throws Exception {
        return drEventsDao.getAllDREvents();
    }


    @RequestMapping(value = "/DREvents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  List<SmDREvent> getDREvents(HttpServletRequest request) throws Exception {
        return  drEventsDao.getPersonalDREvents(securityService.getAccount().getIdAccount());
    }
}
