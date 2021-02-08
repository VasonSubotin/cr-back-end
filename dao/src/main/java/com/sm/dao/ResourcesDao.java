package com.sm.dao;

import com.sm.model.SmResource;

import java.util.List;

public interface ResourcesDao {
    List<SmResource> getAllResourceByAccountId(Long accountId);

    SmResource getResourceByIdAndAccountId(Long id,Long accountId) ;

    SmResource getResourceByExternalIdAndAccountId(String vExternal, Long accountId);

    SmResource saveResource(SmResource smResource, Long accountId);

    SmResource deleteResourceByIdAndAccountId(Long id, Long accountId);

    String getImageByResource(SmResource smResource);
}
