package com.sm.dao.cache;

import com.sm.model.Constants;
import com.sm.model.SmAccount;
import com.sm.model.SmEvent;
import com.sm.model.SmartCarCache;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
@Component
public class SmartCarCacheDaoImpl implements SmartCarCacheDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public SmartCarCache getSmartCarCache(String externalResourceId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmartCarCache where externalResourceId=:externalResourceId";
        Query query = session.createQuery(hql);
        query.setParameter("externalResourceId", externalResourceId);
        List ret = query.getResultList();
        return ret.isEmpty() ? null : (SmartCarCache) ret.get(0);
    }


    @Override
    public List<SmartCarCache> getSmartCarCacheIn(Collection<String> externalResourceIds) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmartCarCache where externalResourceId IN (:externalResourceIds)";
        Query query = session.createQuery(hql);
        query.setParameter("externalResourceIds", externalResourceIds);
        return query.getResultList();
    }

    @Transactional(readOnly = false)
    @Override
    public void saveSmartCarCache(SmartCarCache smartCarCache) {
        synchronized (Constants.class) {
             sessionFactory.getCurrentSession().save(smartCarCache);
        }
    }
}
