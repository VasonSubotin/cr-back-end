package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.client.services.TimeOfUsageService;
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

    @Autowired
    private TimeOfUsageService timeOfUsageService;

    @RequestMapping(value = "/tous/{tou_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage getTimeOfUsageById(HttpServletRequest request, @PathVariable("tou_id") long touId) throws Exception {
        return timeOfUsageService.getTimeOfUsageById(touId);
    }

    @RequestMapping(value = "/tous/{tou_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage deleteTimeOfUsageById(HttpServletRequest request, @PathVariable("tou_id") long touId) throws Exception {
        return timeOfUsageService.deleteTimeOfUsageById(touId);
    }

    @RequestMapping(value = "/tous", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage saveTimeOfUsage(HttpServletRequest request, @RequestBody SmTimeOfUsage smTimeOfUsage) throws Exception {
        return timeOfUsageService.saveTimeOfUsageByResourceId(smTimeOfUsage.getResourceId(), smTimeOfUsage);
    }

    @RequestMapping(value = "/resources/{resource_id}/tou", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage getTimeOfUsageByResource(HttpServletRequest request, @PathVariable("resource_id") Long resourceId) throws Exception {
        return timeOfUsageService.getTimeOfUsageByResourceId(resourceId);
    }

    @RequestMapping(value = "/resources/{resource_id}/tou", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage updateTimeOfUsageByResource(HttpServletRequest request, @RequestBody SmTimeOfUsage smTimeOfUsage, @PathVariable("resource_id") Long resourceId) throws Exception {
        return timeOfUsageService.updateTimeOfUsageByResourceId(resourceId, smTimeOfUsage);
    }

    @RequestMapping(value = "/tous/{tou_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage updateTimeOfUsage(HttpServletRequest request, @RequestBody SmTimeOfUsage smTimeOfUsage, @PathVariable("tou_id") Long touId) throws Exception {
        return timeOfUsageService.updateTimeOfUsage(touId, smTimeOfUsage);
    }

    @RequestMapping(value = "/tous", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmTimeOfUsage> getTimeOfUsagesAll(HttpServletRequest request) throws Exception {
        return timeOfUsageDao.getAllSmTimeOfUsages();
    }

    @RequestMapping(value = "/touPersonalList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmTimeOfUsage> getTimeOfUsages(HttpServletRequest request) throws Exception {
        return timeOfUsageService.getAllPersonalTimeOfUsages();
    }
}
