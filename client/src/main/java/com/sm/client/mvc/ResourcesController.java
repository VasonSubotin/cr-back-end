package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.AccountsDao;
import com.sm.dao.ResourcesDao;
import com.sm.model.SmAccount;
import com.sm.model.SmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ResourcesController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ResourcesDao resourcesDao;

    @RequestMapping(value = "/resources/{resource_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResource getUserResourcesById(HttpServletRequest request, @PathVariable("resource_id") int resourceId) throws Exception {
        return resourcesDao.getResourceByIdAndAccountId(new Long(resourceId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/resources/{resource_id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUserResourcesById(HttpServletRequest request, @PathVariable("resource_id") int resourceId) throws Exception {
        resourcesDao.deleteResourceByIdAndAccountId(new Long(resourceId), securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/resources", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public SmResource saveResource(HttpServletRequest request, @RequestBody SmResource smResource) throws Exception {
        return resourcesDao.saveResource(smResource, securityService.getAccount().getIdAccount());
    }

    @RequestMapping(value = "/resourcesList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmResource> getUserResources(HttpServletRequest request) throws Exception {
        return resourcesDao.getAllResourceByAccountId(securityService.getAccount().getIdAccount());
    }

}
