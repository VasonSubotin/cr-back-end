package com.sm.dao;

import com.sm.model.Constants;
import com.sm.model.SmDREvent;
import com.sm.model.SmTimeOfUsage;
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
public class DREventsDaoImpl implements DREventsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmDREvent> getAllDREvents() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmDREvent where deleted=0 or deleted is null";
            return session.createQuery(hql).getResultList();
        }
    }

    @Override
    public List<SmDREvent> getDREventsByResourceId(Long resourceId) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmDREvent where (deleted=0 or deleted is null) and resourceId=:resourceId";
            Query query = session.createQuery(hql);
            query.setParameter("resourceId", resourceId);

            return query.getResultList();
        }
    }

}
