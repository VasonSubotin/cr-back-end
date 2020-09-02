package com.sm.dao;


import com.sm.model.SmTimeOfUsage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Component
public class TimeOfUsageDaoImpl implements TimeOfUsageDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmTimeOfUsage> getAllSmTimeOfUsages() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmTimeOfUsage where deleted=0 or deleted is null";
        return session.createQuery(hql).getResultList();
    }

    @Override
    public List<SmTimeOfUsage> getTimeOfUsagesByResourceIn(List<Long> resourceIds) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmTimeOfUsage where (deleted=0 or deleted is null) and resourceId in :resourceIds";
        Query query = session.createQuery(hql);
        query.setParameter("resourceIds", resourceIds);
        return query.getResultList();
    }

    @Override
    public SmTimeOfUsage getTimeOfUsageById(Long id) {
        return sessionFactory.getCurrentSession().get(SmTimeOfUsage.class, id);
    }

    @Override
    public SmTimeOfUsage getTimeOfUsageByResourceId(Long resourceId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmTimeOfUsage where (deleted=0 or deleted is null) and resourceId=:resourceId";
        Query query = session.createQuery(hql);

        query.setParameter("resourceId", resourceId);
        List results = query.getResultList();

        return results.isEmpty() ? null : (SmTimeOfUsage) results.get(0);
    }

    @Transactional(readOnly = false)
    @Override
    public SmTimeOfUsage saveTimeOfUsage(SmTimeOfUsage smTimeOfUsage) {
        smTimeOfUsage.setIdTou((Long) sessionFactory.getCurrentSession().save(smTimeOfUsage));
        return smTimeOfUsage;
    }

    @Override
    @Transactional(readOnly = false)
    public SmTimeOfUsage deleteTimeOfUsageById(Long id) {

        Query query = sessionFactory.getCurrentSession().createQuery("update SmTimeOfUsage set deleted = 1 where idTou = :id");
        query.setParameter("id", id);
        query.executeUpdate();
        return getTimeOfUsageById(id);
    }
}
