package com.sm.client.services;

import com.sm.dao.ResourcesDao;
import com.sm.dao.SessionsDao;
import com.sm.dao.cache.SmartCarCacheDao;
import com.sm.model.SmResource;
import com.sm.model.SmSession;
import com.sm.model.SmartCarCache;
import com.sm.model.web.RecourseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private SessionsDao sessionsDao;

    @Autowired
    private ResourcesDao resourcesDao;

    @Autowired
    private SmartCarCacheDao smartCarCacheDao;

//    @Autowired
//    private CommonDao commonDao;
//
//    private Map<Long, String> statusMap = new ConcurrentHashMap<>();
//
//    @PostConstruct
//    public void init() {
//        commonDao.getAllSessionTypes()
//    }

    @Override
    public RecourseInfo getRecourseInfo(Long accountId, Long resourceId) {
        RecourseInfo recourseInfo = new RecourseInfo();

        SmSession smSession = sessionsDao.getActiveSessionByAccountIdAndResourceId(accountId, resourceId);
        if (smSession != null) {
            recourseInfo.setCarbonImpact(smSession.getCarbonImpact());
            recourseInfo.setPower(smSession.getEnergy());
            recourseInfo.setStatus(smSession.getStatus());
            recourseInfo.setDtUpdated(smSession.getDtUpdated());
        }

        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(resourceId, accountId);
        if (smResource != null) {
            recourseInfo.setCapacity(smResource.getCapacity());
            recourseInfo.setGroupId(smResource.getGroupId());
            recourseInfo.setIdResource(smResource.getIdResource());
            recourseInfo.setModel(smResource.getModel());
            recourseInfo.setVendor(smResource.getVendor());
            recourseInfo.setResourceTypeId(smResource.getResourceTypeId());
            recourseInfo.setDtCreated(smResource.getDtCreated());
            recourseInfo.setPolicyId(smResource.getPolicyId());
            recourseInfo.setnChargeByTime(smResource.getnChargeByTime());
        }

        return recourseInfo;
    }


    @Override
    public SmartCarCache getSmartCarCache(String vin) {
        return smartCarCacheDao.getSmartCarCache(vin);
    }

    @Override
    public List<SmartCarCache> getSmartCarCacheIn(Collection<String> vins) {
        return smartCarCacheDao.getSmartCarCacheIn(vins);
    }
}
