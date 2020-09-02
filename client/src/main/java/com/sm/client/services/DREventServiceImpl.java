package com.sm.client.services;

import com.sm.dao.DREventsDao;
import com.sm.dao.ResourcesDao;
import com.sm.model.SmDREvent;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DREventServiceImpl implements DREventService {

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private DREventsDao drEventsDao;

    @Override
    public List<SmDREvent> getDREventsByResourceId(Long resourceId) throws SmException {
        Long accountId = securityService.getAccount().getIdAccount();

        //checking resourceId
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, accountId);
        if (smResource == null) {
            throw new SmException("Resource with resourceId=" + resourceId + " does not exists for current user[accountId=" + accountId + "].", HttpStatus.SC_FORBIDDEN);
        }

        return drEventsDao.getDREventsByResourceId(resourceId);
    }

}
