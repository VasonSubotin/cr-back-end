package com.sm.dao;

import com.sm.model.SmDREvent;
import com.sm.model.SmTimeOfUsage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DREventsDaoImpl implements DREventsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmDREvent> getAllDREvents() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmDREvent where deleted=0 or deleted is null";
        return session.createQuery(hql).getResultList();
    }

    @Override
    public List<SmDREvent> getPersonalDREvents(Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmDREvent where (deleted=0 or deleted is null) and accountId=:accountId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

}
