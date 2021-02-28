package com.sm.dao;

import com.sm.model.Constants;
import com.sm.model.SmResource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
@Component
public class ResourcesDaoImpl implements ResourcesDao {

    @Value("${smartcar.resources.imagesPath:/}")
    private String pathPrefix = "/";

    @Value("${smartcar.resources.imagesExt:.jpg}")
    private String imagesExt = ".jpg";

    @Value("${smartcar.resources.imagesUrl:}")
    private String imagesUrl = "";

    @Value("${smartcar.resources.imagesGeneric:generic}")
    private String imagesGeneric = "generic";

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmResource> getAllResourceByAccountId(Long accountId) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmResource where (deleted=0 or deleted is null) and accountId=:accountId";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            return calcImage(query.getResultList());
        }
    }

    @Override
    public SmResource getResourceByIdAndAccountId(Long id, Long accountId) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmResource where (deleted=0 or deleted is null) and accountId=:accountId and idResource=:idResource";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            query.setParameter("idResource", id);
            List<SmResource> result = query.getResultList();

            return (result == null || result.isEmpty()) ? null : calcImage(result.iterator().next());
        }
    }

    @Override
    public SmResource getResourceByExternalIdAndAccountId(String vExternal, Long accountId) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();

            String hql = "FROM SmResource where (deleted=0 or deleted is null) and externalResourceId=:vExternal ";
            if (accountId != null) {
                hql += "and accountId=:accountId";
            } else {
                hql += "and accountId is null";
            }
            Query query = session.createQuery(hql);
            query.setParameter("vExternal", vExternal);
            if (accountId != null) {
                query.setParameter("accountId", accountId);
            }
            List<SmResource> result = query.getResultList();

            return (result == null || result.isEmpty()) ? null : calcImage(result.iterator().next());
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public SmResource saveResource(SmResource smResource, Long accountId) {

            smResource.setAccountId(accountId);
            smResource.setIdResource((Long) sessionFactory.getCurrentSession().save(smResource));
            return calcImage(smResource);

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public SmResource updateResource(SmResource smResource, Long accountId) {

        smResource.setAccountId(accountId);
        sessionFactory.getCurrentSession().merge(smResource);
        // smResource.setIdResource((Long) sessionFactory.getCurrentSession().save(smResource));
        return calcImage(smResource);

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public SmResource deleteResourceByIdAndAccountId(Long id, Long accountId) {

            //sessionFactory.getCurrentSession().update("UPDATE SmResource set SmResource.deleted");
            Query query = sessionFactory.getCurrentSession().createQuery("update SmResource set deleted = 1 where idResource = :id and accountId=:accountId");
            query.setParameter("id", id);
            query.setParameter("accountId", accountId);
            query.executeUpdate();
            return calcImage(getResourceByIdAndAccountId(id, accountId));

    }

    @Override
    public String getImageByResource(SmResource smResource) {
        String vendorModel = (smResource.getVendor() + "/" + smResource.getModel() + imagesExt).toLowerCase().replaceAll(" ", "");
        if (!new File(pathPrefix + "/" + vendorModel).exists()) {
            // looking for vendor
            String vendor = (smResource.getVendor() + "/" + imagesGeneric + imagesExt).toLowerCase().replaceAll(" ", "");
            if (!new File(pathPrefix + "/" + vendor).exists()) {
                return imagesUrl + (imagesGeneric + imagesExt).toLowerCase().replaceAll(" ", "");
            }
            return imagesUrl + vendor;
        }
        return imagesUrl + vendorModel;
    }

    private SmResource calcImage(SmResource smResource) {
        if (smResource != null) {
            smResource.setImagePath(getImageByResource(smResource));
        }
        return smResource;
    }

    private List<SmResource> calcImage(List<SmResource> smResources) {
        if (smResources != null) {
            for (SmResource smResource : smResources) {
                calcImage(smResource);
            }
        }
        return smResources;
    }
}

