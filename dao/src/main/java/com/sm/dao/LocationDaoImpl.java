package com.sm.dao;

import com.sm.model.Constants;
import com.sm.model.SmLocation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Component
@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
public class LocationDaoImpl implements LocationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmLocation> getAllLocations() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmLocation where deleted=0 or deleted is null";
            return session.createQuery(hql).getResultList();
        }
    }

    @Override
    public List<SmLocation> getAllPersonalLocations(Long accountId) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmLocation where (deleted=0 or deleted is null) and accountId=:accountId";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            return query.getResultList();
        }
    }

    @Override
    public SmLocation getLocationByIdAndAccountId(Long id, Long accountId) {
        synchronized (Constants.class) {
            return sessionFactory.getCurrentSession().get(SmLocation.class, id);
        }
    }

    @Override
    public SmLocation getLocationByExternalIdAndAccountId(String externalLocationId, Long accountId) {
        synchronized (Constants.class) {
            synchronized (Constants.class) {
                Session session = sessionFactory.getCurrentSession();
                String hql = "FROM SmLocation where (deleted=0 or deleted is null) and accountId=:accountId and externalLocationId=:externalLocationId";
                Query query = session.createQuery(hql);
                query.setParameter("accountId", accountId);
                query.setParameter("externalLocationId", externalLocationId);
                List<SmLocation> res = query.getResultList();
                return res==null || res.isEmpty()?null:res.iterator().next();
            }
        }
    }


    @Override
    public List<SmLocation> getLocationsInSmallRangeAndAccountId(Long accountId, double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmLocation where (deleted=0 or deleted is null) AND (accountId=:accountId OR accountId is null) AND latitude between :latitudeA and :latitudeB AND longitude between :longitudeA and  :longitudeB";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            query.setParameter("latitudeA", latitudeA);
            query.setParameter("latitudeB", latitudeB);
            query.setParameter("longitudeA", longitudeA);
            query.setParameter("longitudeB", longitudeB);
            return query.getResultList();
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public SmLocation saveLocation(SmLocation smLocation, Long accountId) {
        synchronized (Constants.class) {
            smLocation.setIdLocation((Long) sessionFactory.getCurrentSession().save(smLocation));
            smLocation.setAccountId(accountId);
            return smLocation;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public SmLocation deleteLocationById(Long id, Long accountId) {
        synchronized (Constants.class) {
            Query query = sessionFactory.getCurrentSession().createQuery("update SmLocation set deleted = 1 where idLocation = :id and accountId=:accountId");
            query.setParameter("id", id);
            query.setParameter("accountId", accountId);
            query.executeUpdate();
            return getLocationByIdAndAccountId(id, accountId);
        }
    }
}
//Unable to commit against JDBC Connection; nested exception is org.hibernate.TransactionException: Unable to commit against JDBC Connection