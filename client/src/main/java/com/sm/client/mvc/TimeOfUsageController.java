package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.TimeOfUsageDao;
import com.sm.model.SmTimeOfUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class TimeOfUsageController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private TimeOfUsageDao timeOfUsageDao;


    @RequestMapping(value = "/tous/{tou_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage getTimeOfUsageById(HttpServletRequest request, @PathVariable("tou_id") long touId) throws Exception {
        return timeOfUsageDao.getTimeOfUsageByIdAndAccountId(touId, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/tous/{tou_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage deleteTimeOfUsageById(HttpServletRequest request, @PathVariable("tou_id") long touId) throws Exception {
        return timeOfUsageDao.deleteTimeOfUsageById(touId, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/tous", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage saveTimeOfUsage(HttpServletRequest request, @RequestBody SmTimeOfUsage smTimeOfUsage) throws Exception {
        return timeOfUsageDao.saveTimeOfUsage(smTimeOfUsage, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/tous", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmTimeOfUsage> getTimeOfUsagesAll(HttpServletRequest request) throws Exception {
        return timeOfUsageDao.getAllSmTimeOfUsages();
    }

    @RequestMapping(value = "/touPersonalList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmTimeOfUsage> getTimeOfUsages(HttpServletRequest request) throws Exception {
        return timeOfUsageDao.getAllPersonalTimeOfUsages(securityService.getAccount().getIdAccount());
    }
}
