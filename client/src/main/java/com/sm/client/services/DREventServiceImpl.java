package com.sm.client.services;

import com.sm.dao.DREventsDao;
import com.sm.dao.ResourcesDao;
import com.sm.model.SmDREvent;
import com.sm.model.SmException;
import com.sm.model.SmResource;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DREventServiceImpl implements DREventService {

    private static final Logger logger = LoggerFactory.getLogger(DREventServiceImpl.class);

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

    @Override
    public List<SmDREvent> updateDREventsByResourceId(Long resourceId, List<SmDREvent> smDREvents) throws SmException {
        List<SmDREvent> eventsTOSave = new ArrayList<>();
        try {
            List<SmDREvent> smDREventListExists = getDREventsByResourceId(resourceId);
            Map<Long, SmDREvent> drEventExistsMap = smDREventListExists.stream().collect(Collectors.toMap(a -> a.getIdDrEvent(), a -> a));

            for (SmDREvent smDREventToUpdate : smDREvents) {
                SmDREvent smDREventExists = drEventExistsMap.get(smDREventToUpdate.getIdDrEvent());
                if (smDREventExists == null) {
                    logger.warn("Can't find DREvent by id {} - will not update item", smDREventToUpdate.getIdDrEvent());
                    continue;
                }
                if (smDREventToUpdate.getResourceId() != null && smDREventToUpdate.getResourceId() != resourceId) {
                    logger.warn("DREvent with id {}  has resourceId {} which does not match with requested resourceId {} - will not update item", smDREventToUpdate.getIdDrEvent(), smDREventToUpdate.getResourceId(), resourceId);
                    continue;
                }
                eventsTOSave.add(updateSmDREvent(smDREventExists, smDREventToUpdate));
            }
            drEventsDao.saveOrUpdateDREvents(eventsTOSave);
            return eventsTOSave;
        } catch (SmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new SmException(ex.getMessage(), 500);
        }
    }

    private SmDREvent updateSmDREvent(SmDREvent smDREventExists, SmDREvent smDREventToUpdate) {
        if (smDREventToUpdate.getStart() != null) {
            smDREventExists.setStart(smDREventToUpdate.getStart());
        }
        if (smDREventToUpdate.getStop() != null) {
            smDREventExists.setStop(smDREventToUpdate.getStop());
        }
        if (smDREventToUpdate.getDeleted() != null) {
            smDREventExists.setDeleted(smDREventToUpdate.getDeleted());
        }
        if (smDREventToUpdate.getDtCreated() != null) {
            smDREventExists.setDtCreated(smDREventToUpdate.getDtCreated());
        }
        if (smDREventToUpdate.getLocationId() != null) {
            smDREventExists.setLocationId(smDREventToUpdate.getLocationId());
        }
//        if (smDREventToUpdate.getTimeZoneIndex() != null) {
//            smDREventExists.setTimeZoneIndex(smDREventToUpdate.getTimeZoneIndex());
//        }
        return smDREventExists;
    }
}
