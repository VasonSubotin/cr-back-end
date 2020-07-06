package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.AccountsDao;
import com.sm.dao.ResourcesDao;
import com.sm.model.SmAccount;
import com.sm.model.SmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@RestController
public class ResourcesController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ResourcesDao resourcesDao;

    @RequestMapping(value = "/resources/{resource_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResource getUserResourcesById(HttpServletRequest request, HttpServletResponse response, @PathVariable("resource_id") int resourceId) throws Exception {
        response.setStatus(HttpStatus.CREATED.value());
        return resourcesDao.getResourceByIdAndAccountId(new Long(resourceId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/resources/{resource_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUserResourcesById(HttpServletRequest request, HttpServletResponse response, @PathVariable("resource_id") int resourceId) throws Exception {
        response.setStatus(HttpStatus.NO_CONTENT.value());
        resourcesDao.deleteResourceByIdAndAccountId(new Long(resourceId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/resources", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResource saveResource(HttpServletRequest request, HttpServletResponse response, @RequestBody SmResource smResource) throws Exception {
        return resourcesDao.saveResource(smResource, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/resources", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResource saveResourceUpadte(HttpServletRequest request, HttpServletResponse response, @RequestBody SmResource smResource) throws Exception {
        Long accountId = securityService.getAccount().getIdAccount();
        SmResource smResourceExists = resourcesDao.getResourceByIdAndAccountId(smResource.getIdResource(), accountId);
        if (smResourceExists == null) {
            smResource.setDtCreated(new Date());
            response.setStatus(HttpStatus.CREATED.value());
            return resourcesDao.saveResource(smResource,accountId);
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
        smResourceExists.setDtUpdated(new Date());
        return resourcesDao.saveResource(smResourceExists,accountId);
    }


    @RequestMapping(value = "/resources", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmResource> getUserResources(HttpServletRequest request) throws Exception {
        return resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount());
    }

}
