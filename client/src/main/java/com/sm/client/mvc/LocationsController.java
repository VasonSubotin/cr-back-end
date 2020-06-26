package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.LocationDao;
import com.sm.model.SmLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class LocationsController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private LocationDao locationDao;

    @RequestMapping(value = "/locations/{location_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmLocation getUserResourcesById(HttpServletRequest request, @PathVariable("location_id") int locationId) throws Exception {
        return locationDao.getLocationByIdAndAccountId(new Long(locationId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/locations/{location_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmLocation deleteUserResourcesById(HttpServletRequest request, @PathVariable("location_id") int locationId) throws Exception {
        return locationDao.deleteLocationById(new Long(locationId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/locations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmLocation saveResource(HttpServletRequest request, @RequestBody SmLocation smLocation) throws Exception {
        return locationDao.saveLocation(smLocation, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/locations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmLocation> getUserResources(HttpServletRequest request, @RequestParam(value = "accountId", required = false) Long accountId) throws Exception {
        return locationDao.getAllLocations();
    }

    @RequestMapping(value = "/locationsPersonal", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmLocation> getUserResources(HttpServletRequest request) throws Exception {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        // return locationDao.getAllPersonalLocations(accountId);
        return null;
    }
}
