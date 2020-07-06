package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.LocationDao;
import com.sm.model.SmLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class LocationsController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private LocationDao locationDao;

    @RequestMapping(value = "/locations/{location_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmLocation getUserResourcesById(HttpServletRequest request, HttpServletResponse response, @PathVariable("location_id") int locationId) throws Exception {
        return locationDao.getLocationByIdAndAccountId(new Long(locationId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/locations/{location_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmLocation deleteUserResourcesById(HttpServletRequest request, HttpServletResponse response, @PathVariable("location_id") int locationId) throws Exception {
        response.setStatus(HttpStatus.NO_CONTENT.value());
        return locationDao.deleteLocationById(new Long(locationId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/locations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmLocation saveResource(HttpServletRequest request, HttpServletResponse response, @RequestBody SmLocation smLocation) throws Exception {
        response.setStatus(HttpStatus.CREATED.value());
        return locationDao.saveLocation(smLocation, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/locations", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmLocation saveResourceUpdate(HttpServletRequest request, HttpServletResponse response, @RequestBody SmLocation smLocation) throws Exception {
        Long accountId = securityService.getAccount().getIdAccount();
        SmLocation smLocationExists = locationDao.getLocationByIdAndAccountId(smLocation.getIdLocation(), accountId);
        if (smLocationExists == null) {
            smLocation.setDeleted(false);
            response.setStatus(HttpStatus.CREATED.value());
            return locationDao.saveLocation(smLocation, accountId);
        }
        if (smLocation.getLatitude() != null) {
            smLocationExists.setLatitude(smLocation.getLatitude());
        }
        if (smLocation.getLongitude() != null) {
            smLocationExists.setLongitude(smLocation.getLongitude());
        }
        if (smLocation.getDescription() != null) {
            smLocationExists.setDescription(smLocation.getDescription());
        }
        if (smLocation.getTimeZone() != null) {
            smLocationExists.setTimeZone(smLocation.getTimeZone());
        }

        response.setStatus(HttpStatus.OK.value());
        return locationDao.saveLocation(smLocationExists, accountId);
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
