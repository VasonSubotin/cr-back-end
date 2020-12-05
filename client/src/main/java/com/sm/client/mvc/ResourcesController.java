package com.sm.client.mvc;

import com.sm.client.model.smartcar.SmResourceState;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.CommonService;
import com.sm.client.services.ResourceService;
import com.sm.client.services.SecurityService;
import com.sm.client.services.SmartCarService;
import com.sm.client.services.cache.SmartCarCacheService;
import com.sm.dao.ResourcesDao;
import com.sm.model.*;
import com.sm.model.web.RecourseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class ResourcesController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private CommonService commonService;

//    @Autowired
//    private SmartCarService smartCarService;

    @Autowired
    private SmartCarCacheService smartCarCacheService;

    @Autowired
    private ResourceService resourceService;

    @RequestMapping(value = "/resources/{resource_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResourceState getUserResourcesById(HttpServletRequest request, HttpServletResponse response, @PathVariable("resource_id") long resourceId) throws Exception {
        response.setStatus(HttpStatus.OK.value());
        return resourceService.getResourceState(resourceId);
    }

    @RequestMapping(value = "/resources/{resource_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUserResourcesById(HttpServletRequest request, HttpServletResponse response, @PathVariable("resource_id") long resourceId) throws Exception {
        response.setStatus(HttpStatus.NO_CONTENT.value());
        resourcesDao.deleteResourceByIdAndAccountId(resourceId, securityService.getAccount().getIdAccount());
        securityService.deleteSession(Constants.SMART_CAR_AUTH_TYPE, resourceId);
    }

    @RequestMapping(value = "/resources", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResource saveResource(HttpServletRequest request, HttpServletResponse response, @RequestBody SmResource smResource) throws Exception {
        return resourcesDao.saveResource(smResource, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/resources/{resource_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResource saveResourceUpadte(HttpServletRequest request, HttpServletResponse response, @RequestBody SmResource smResource, @PathVariable("resource_id") Long resourceId) throws Exception {
        Long accountId = securityService.getAccount().getIdAccount();
        SmResource smResourceExists = resourcesDao.getResourceByIdAndAccountId(resourceId, accountId);
        if (smResourceExists == null) {
            smResource.setDtCreated(new Date());
            response.setStatus(HttpStatus.CREATED.value());
            return resourcesDao.saveResource(smResource, accountId);
        }

        if (smResource.getExternalResourceId() != null) {
            smResourceExists.setExternalResourceId(smResource.getExternalResourceId());
        }
        if (smResource.getCapacity() != null) {
            smResourceExists.setCapacity(smResource.getCapacity());
        }
        if (smResource.getGroupId() != null) {
            smResourceExists.setGroupId(smResource.getGroupId());
        }
        if (smResource.getModel() != null) {
            smResourceExists.setModel(smResource.getModel());
        }
        if (smResource.getPolicyId() != null) {
            smResourceExists.setPolicyId(smResource.getPolicyId());
        }
        if (smResource.getPower() != null) {
            smResourceExists.setPower(smResource.getPower());
        }
        if (smResource.getResourceTypeId() != null) {
            smResourceExists.setResourceTypeId(smResource.getResourceTypeId());
        }
        if (smResource.getVendor() != null) {
            smResourceExists.setVendor(smResource.getVendor());
        }
        if (smResource.getIdResource() != null) {
            smResourceExists.setIdResource(smResource.getIdResource());
        }
        if (smResource.getnChargeByTime() != null) {
            smResourceExists.setnChargeByTime(smResource.getnChargeByTime());
        }
        smResourceExists.setDtUpdated(new Date());
        return resourcesDao.saveResource(smResourceExists, accountId);
    }

//
//    @RequestMapping(value = "/resources", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> getUserResources(HttpServletRequest request) throws Exception {
//        try {
//            return new ResponseEntity(resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount()), HttpStatus.OK);
//        } catch (SmException smEx) {
//            return new ResponseEntity(new ServiceResult(smEx.getCode(), HttpStatus.resolve(smEx.getCode()).getReasonPhrase(), smEx.getMessage(), "/resources"), HttpStatus.resolve(smEx.getCode()));
//        }
//    }


    @RequestMapping(value = "/resources", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserResources(HttpServletRequest request) throws Exception {
        try {
            // return new ResponseEntity(resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount()), HttpStatus.OK);
            return new ResponseEntity(resourceService.getResourceStates(), HttpStatus.OK);
        } catch (SmException smEx) {
            return new ResponseEntity(new ServiceResult(smEx.getCode(), HttpStatus.resolve(smEx.getCode()).getReasonPhrase(), smEx.getMessage(), "/resources"), HttpStatus.resolve(smEx.getCode()));
        }
    }

    @RequestMapping(value = "/resources/{resource_id}/resourceInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRecourseInfo(HttpServletRequest request, HttpServletResponse response, @PathVariable("resource_id") long resourceId) throws SmException {
        try {
            return new ResponseEntity(commonService.getRecourseInfo(securityService.getAccount().getIdAccount(), resourceId), HttpStatus.OK);
        } catch (SmException smEx) {
            return new ResponseEntity(new ServiceResult(smEx.getCode(), HttpStatus.resolve(smEx.getCode()).getReasonPhrase(), smEx.getMessage(), "/resources/" + resourceId + "/stateInfo"), HttpStatus.resolve(smEx.getCode()));
        }
    }

    @RequestMapping(value = "/resources/stateInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRecoursesState(HttpServletRequest request, HttpServletResponse response) throws SmException {
        try {
            return new ResponseEntity(smartCarCacheService.getResourcesStatesByAccount(), HttpStatus.OK);
        } catch (SmException smEx) {
            return new ResponseEntity(new ServiceResult(smEx.getCode(), HttpStatus.resolve(smEx.getCode()).getReasonPhrase(), smEx.getMessage(), "/resources/stateInfo"), HttpStatus.resolve(smEx.getCode()));
        }
    }

    @RequestMapping(value = "/resources/{resource_id}/stateInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRecourseState(HttpServletRequest request, HttpServletResponse response, @PathVariable("resource_id") long resourceId) throws SmException {
        try {
            return new ResponseEntity(smartCarCacheService.getResourceState(resourceId), HttpStatus.OK);
        } catch (SmException smEx) {
            return new ResponseEntity(new ServiceResult(smEx.getCode(), HttpStatus.resolve(smEx.getCode()).getReasonPhrase(), smEx.getMessage(), "/resources/" + resourceId + "/stateInfo"), HttpStatus.resolve(smEx.getCode()));
        }
    }

    @RequestMapping(value = "/resources/{resource_id}/getCarImage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCarImage(HttpServletRequest request, @PathVariable("resource_id") long resourceId) throws Exception {
        return resourceService.getResourceImage(resourceId);
    }
}
