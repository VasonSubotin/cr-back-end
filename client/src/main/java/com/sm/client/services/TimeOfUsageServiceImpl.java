package com.sm.client.services;

import com.sm.dao.ResourcesDao;
import com.sm.dao.TimeOfUsageDao;
import com.sm.model.SmAccount;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import com.sm.model.SmTimeOfUsage;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeOfUsageServiceImpl implements TimeOfUsageService {

    @Autowired
    private TimeOfUsageDao timeOfUsageDao;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SecurityService securityService;

    @Override
    public SmTimeOfUsage getTimeOfUsageById(Long touId) throws SmException {
        return getTouForCurrentUserByTou(touId);
    }


    @Override
    public SmTimeOfUsage deleteTimeOfUsageById(Long touId) throws SmException {
        //checking that it exist
        getTouForCurrentUserByTou(touId);
        return timeOfUsageDao.deleteTimeOfUsageById(touId);
    }

    @Override
    public SmTimeOfUsage getTimeOfUsageByResourceId(Long resourceId) throws SmException {

        SmAccount smAccount = securityService.getAccount();
        SmResource resource = resourcesDao.getResourceByIdAndAccountId(resourceId, smAccount.getIdAccount());
        if (resource == null) {
            throw new SmException("Can't find resource by resourceId=" + resourceId + " for user " + smAccount.getLogin(), HttpStatus.SC_NOT_FOUND);
        }

        return timeOfUsageDao.getTimeOfUsageByResourceId(resourceId);
    }

    @Override
    public List<SmTimeOfUsage> getAllPersonalTimeOfUsages() throws SmException {
        SmAccount smAccount = securityService.getAccount();
        List<SmResource> resources = resourcesDao.getAllResourceByAccountId(smAccount.getIdAccount());
        if (resources == null || resources.isEmpty()) {
            throw new SmException("No resources found for user " + smAccount.getLogin(), HttpStatus.SC_NOT_FOUND);
        }
        return timeOfUsageDao.getTimeOfUsagesByResourceIn(resources.stream().map(SmResource::getIdResource).collect(Collectors.toList()));
    }

    @Override
    public SmTimeOfUsage saveTimeOfUsageByResourceId(Long resourceId, SmTimeOfUsage smTimeOfUsage) throws SmException {
        Long accountId = securityService.getAccount().getIdAccount();

        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, accountId);
        if (smResource == null) {
            throw new SmException("Resource with resourceId=" + resourceId + " does not exists for current user[accountId=" + accountId + "]. Please call GET /touPersonalList to see all tous and resource IDs for current user.", HttpStatus.SC_FORBIDDEN);
        }

        SmTimeOfUsage existing = timeOfUsageDao.getTimeOfUsageByResourceId(resourceId);
        if (existing != null) {
            throw new SmException("There is tou for resourceId=" + resourceId + ". Please use method PUT to update  tou.", HttpStatus.SC_NOT_ACCEPTABLE);
        }

        return timeOfUsageDao.saveTimeOfUsage(smTimeOfUsage);
    }

    @Override
    public SmTimeOfUsage updateTimeOfUsageByResourceId(Long resourceId, SmTimeOfUsage smTimeOfUsage) throws SmException {
        Long accountId = securityService.getAccount().getIdAccount();

        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, accountId);
        if (smResource == null) {
            throw new SmException("Requested tou by resourceId=" + resourceId + " does not belong to current user. Please call GET /touPersonalList to see all tous for current user or use POST /tous to add new one", HttpStatus.SC_FORBIDDEN);
        }

        SmTimeOfUsage existing = timeOfUsageDao.getTimeOfUsageByResourceId(resourceId);
        if (existing == null) {
            throw new SmException("Can't find tou by resourceId=" + resourceId + ". Please call GET /touPersonalList to see all tous for current user or use POST /tous to add new one.", HttpStatus.SC_NOT_FOUND);
        }

        return timeOfUsageDao.saveTimeOfUsage(applyExisting(existing, smTimeOfUsage));
    }

    @Override
    public SmTimeOfUsage updateTimeOfUsage(Long touId, SmTimeOfUsage smTimeOfUsage) throws SmException {
        return timeOfUsageDao.saveTimeOfUsage(applyExisting(getTouForCurrentUserByTou(touId), smTimeOfUsage));
    }

    private SmTimeOfUsage applyExisting(SmTimeOfUsage existing, SmTimeOfUsage smTimeOfUsage) {

        if (smTimeOfUsage.getResourceId() != null) {
            existing.setResourceId(smTimeOfUsage.getResourceId());
        }
        if (smTimeOfUsage.getLocationId() != null) {
            existing.setLocationId(smTimeOfUsage.getLocationId());
        }
//        if (smTimeOfUsage.getIdTou() != null) {
//            existing.setIdTou(smTimeOfUsage.getIdTou());
//        }
        if (smTimeOfUsage.getStart() != null) {
            existing.setStart(smTimeOfUsage.getStart());
        }
        if (smTimeOfUsage.getStop() != null) {
            existing.setStop(smTimeOfUsage.getStop());
        }

        return existing;
    }


    private SmTimeOfUsage getTouForCurrentUserByTou(Long touId) throws SmException {
        Long accountId = securityService.getAccount().getIdAccount();
        SmTimeOfUsage existing = timeOfUsageDao.getTimeOfUsageById(touId);
        if (existing == null) {
            throw new SmException("Can't find tou by id " + touId + ". Please call GET /touPersonalList to see all tous for current user or use POST /tous to add new one.", HttpStatus.SC_NOT_FOUND);
        }

        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(existing.getResourceId(), accountId);
        if (smResource == null) {
            throw new SmException("Tou with touId=" + touId + " does not exists for current user[accountId=" + accountId + "]. Please call GET /touPersonalList to see all tous and resource IDs for current user.", HttpStatus.SC_FORBIDDEN);
        }
        return existing;
    }
}
