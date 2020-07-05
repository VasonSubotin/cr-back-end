package com.sm.dao;

import com.sm.model.SmUserSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class UserSessionDaoImpl implements UserSessionDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmUserSession> getAllSessions() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmUserSession order by dtCreated";
        Query query = session.createQuery(hql);
        return query.getResultList();
    }


    @Override
    public List<SmUserSession> getActiveSessions(Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmUserSession where (closed=0 or closed is null) and accountId=:accountId order by dtCreated";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    @Override
    public SmUserSession getLastSessionsByType(Long accountId, String userSessionType) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmUserSession where accountId=:accountId and sessionType=:userSessionType order by dtCreated";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("userSessionType", userSessionType);
        query.setMaxResults(1);
        List<SmUserSession> lst = query.getResultList();
        return lst.isEmpty() ? null : lst.iterator().next();
    }

    @Override
    public SmUserSession saveSession(SmUserSession smUserSession) {
        smUserSession.setIdUserSession((Long) sessionFactory.getCurrentSession().save(smUserSession));
        return smUserSession;
    }

}