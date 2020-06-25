package com.sm.dao;


import com.sm.model.SmPolicy;
import com.sm.model.SmResourceType;
import com.sm.model.SmSessionType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Component
public class CommonDaoImpl implements CommonDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmPolicy> getAllPoliciesByActive(boolean active) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmPolicy where deleted=0 or deleted is null";
        if (!active) {
            hql = "FROM SmPolicy where deleted=1";
        }
        return session.createQuery(hql).getResultList();
    }

    @Override
    public List<SmPolicy> getAllPolicies() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmPolicy";
        return session.createQuery(hql).getResultList();
    }


    @Override
    public List<SmResourceType> getAllResourceTypes() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmResourceType";
        return session.createQuery(hql).getResultList();
    }

    @Override
    public List<SmSessionType> getAllSessionTypes() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSessionType";
        return session.createQuery(hql).getResultList();
    }


}
