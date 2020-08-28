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

    @RequestMapping(value = "/resources/{resource_id}/tou", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage getTimeOfUsageByResource(HttpServletRequest request, @PathVariable("resource_id") Long resourceId) throws Exception {
        Long accountId = securityService.getAccount().getIdAccount();
        return timeOfUsageDao.getTimeOfUsageByResourceIdAndAccountId(resourceId, accountId);
    }

    @RequestMapping(value = "/resources/{resource_id}/tou", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage updateTimeOfUsageByResource(HttpServletRequest request, @RequestBody SmTimeOfUsage smTimeOfUsage, @PathVariable("resource_id") Long resourceId) throws Exception {
        Long accountId = securityService.getAccount().getIdAccount();
        SmTimeOfUsage existing = timeOfUsageDao.getTimeOfUsageByResourceIdAndAccountId(resourceId, accountId);
        if (existing == null) {
            return null;
        }
        if (smTimeOfUsage.getResourceId() != null) {
            existing.setResourceId(smTimeOfUsage.getResourceId());
        }
        if (smTimeOfUsage.getLocationId() != null) {
            existing.setLocationId(smTimeOfUsage.getLocationId());
        }
        if (smTimeOfUsage.getIdTou() != null) {
            existing.setIdTou(smTimeOfUsage.getIdTou());
        }
        if (smTimeOfUsage.getStart() != null) {
            existing.setStart(smTimeOfUsage.getStart());
        }
        if (smTimeOfUsage.getStop() != null) {
            existing.setStop(smTimeOfUsage.getStop());
        }
        return timeOfUsageDao.saveTimeOfUsage(existing, accountId);
    }

    @RequestMapping(value = "/tous/{tou_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmTimeOfUsage updateTimeOfUsage(HttpServletRequest request, @RequestBody SmTimeOfUsage smTimeOfUsage, @PathVariable("tou_id") Long touId) throws Exception {
        Long accountId = securityService.getAccount().getIdAccount();
        SmTimeOfUsage existing = timeOfUsageDao.getTimeOfUsageByIdAndAccountId(touId, accountId);
        if (existing == null) {
            return timeOfUsageDao.saveTimeOfUsage(smTimeOfUsage, accountId);
        }

        if (smTimeOfUsage.getIdTou() != null) {
            existing.setIdTou(smTimeOfUsage.getIdTou());
        }
        if (smTimeOfUsage.getStart() != null) {
            existing.setStart(smTimeOfUsage.getStart());
        }
        if (smTimeOfUsage.getStop() != null) {
            existing.setStop(smTimeOfUsage.getStop());
        }
        return timeOfUsageDao.saveTimeOfUsage(existing, accountId);
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
