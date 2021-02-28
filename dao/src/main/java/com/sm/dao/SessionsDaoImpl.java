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
public class SessionsDaoImpl implements SessionsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public SmSession getActiveSessionByAccountIdAndResourceId(Long accountId, Long resourceId) {

        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSession where (closed=0 or closed is null) and accountId=:accountId and resourceId=:resourceId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        List<SmSession> result = query.getResultList();

        return result == null || result.isEmpty() ? null : result.iterator().next();

    }


    @Override
    public List<SmSession> getAllSessionsByAccountIdAndResourceIdAndTimeRange(Long accountId, Long resourceId, Date start, Date stop, Long limit) {

        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSession where (closed=0 or closed is null) and accountId=:accountId and resourceId=:resourceId and  dtStart>:start and dtStart<:stop";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        query.setParameter("start", start);
        query.setParameter("stop", stop);
        query.setMaxResults(limit.intValue());
        return query.getResultList();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveSession(SmSession session) {
        sessionFactory.getCurrentSession().save(session);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateSession(SmSession session) {
        sessionFactory.getCurrentSession().merge(session);
    }
}
