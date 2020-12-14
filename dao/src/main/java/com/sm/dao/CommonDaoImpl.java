package com.sm.dao;


import com.sm.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
@Component
public class CommonDaoImpl implements CommonDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmPolicy> getAllPoliciesByActive(boolean active) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmPolicy where deleted=0 or deleted is null";
            if (!active) {
                hql = "FROM SmPolicy where deleted=1";
            }
            return session.createQuery(hql).getResultList();
        }
    }

    @Override
    public List<SmPolicy> getAllPolicies() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmPolicy";
            return session.createQuery(hql).getResultList();
        }
    }


    @Override
    public List<SmResourceType> getAllResourceTypes() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmResourceType";
            return session.createQuery(hql).getResultList();
        }
    }

    @Override
    public List<SmSessionType> getAllSessionTypes() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmSessionType";
            return session.createQuery(hql).getResultList();
        }
    }


    @Override
    public List<SmEventType> getAllEventTypes() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmEventType";
            return session.createQuery(hql).getResultList();
        }
    }

    @Override
    public List<VehicleModel> getVehicles(){
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM VehicleModel where (deleted=0 or deleted is null) and battery > 0 order by year DESC";
            return session.createQuery(hql).getResultList();
        }
    }


    @Override
    public List<SmMoer> getMoerByDateIn(Date start, Date stop, String externalLocationId){
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmMoer where (deleted=0 or deleted is null) externalLocationId=:externalLocationId and (( start < :start  and stop > :start ) or ( start < :stop  and stop > :stop ) ) order by start DESC";
            Query query = session.createQuery(hql);
            query.setParameter("externalLocationId", externalLocationId);
            query.setParameter("start", start);
            query.setParameter("stop", stop);

            return query.getResultList();
        }
    }

}
