package com.sm.client.mvc;

import com.sm.client.services.SecurityService;
import com.sm.dao.CommonDao;
import com.sm.model.SmPolicy;
import com.sm.model.SmResourceType;
import com.sm.model.SmSessionType;
import com.sm.model.SmTimeOfUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CommonController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CommonDao commonDao;

    @RequestMapping(value = "/policiesList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmPolicy> getAllPlicies(HttpServletRequest request) throws Exception {
        return commonDao.getAllPolicies();
    }

    @RequestMapping(value = "/policies?active={active}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmPolicy> getPlicies(HttpServletRequest request, @RequestParam(value = "active", required = false, defaultValue = "true") Boolean active) throws Exception {
        return commonDao.getAllPoliciesByActive(active);
    }

    @RequestMapping(value = "/resourceTypesList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmResourceType> getResourceTypes(HttpServletRequest request) throws Exception {
        return commonDao.getAllResourceTypes();
    }

    @RequestMapping(value = "/sessionTypeList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SmSessionType> getSessionType(HttpServletRequest request) throws Exception {
        return commonDao.getAllSessionTypes();
    }
}
