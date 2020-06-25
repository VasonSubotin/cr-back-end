package com.sm.dao;


import com.sm.model.SmSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
@Component
public class SessionsDaoImpl implements SessionsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmSession> getActiveSessionsByAccountIdAndResourceId(Long accountId, Long resourceId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSession where (closed=0 or closed is null) and accountId=:accountId and resourceId=:resourceId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        return  query.getResultList();
    }


    @Override
    public List<SmSession> getAllSessionsByAccountIdAndResourceIdAndTimeRange(Long accountId, Long resourceId, Date start, Date stop, Long limit) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSession where (closed=0 or closed is null) and accountId=:accountId and resourceId=:resourceId and  dtStart>:start and dtStart<:stop limit :limit";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        query.setParameter("start", start);
        query.setParameter("stop", stop);
        query.setParameter("limit", limit);
        return query.getResultList();
    }

}
