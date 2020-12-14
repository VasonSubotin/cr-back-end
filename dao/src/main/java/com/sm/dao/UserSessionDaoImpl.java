package com.sm.dao;

import com.sm.model.Constants;
import com.sm.model.SmUserSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
public class UserSessionDaoImpl implements UserSessionDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmUserSession> getAllSessions() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmUserSession order by dtCreated";
            Query query = session.createQuery(hql);
            return query.getResultList();
        }
    }


    @Override
    public List<SmUserSession> getActiveSessions(Long accountId) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmUserSession where (closed=0 or closed is null) and accountId=:accountId order by dtCreated desc";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            return query.getResultList();
        }
    }

    @Override
    public List<SmUserSession> getSessionsByToken(Long accountId, String token) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmUserSession where accountId=:accountId and token=:token order by dtCreated desc";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            query.setParameter("token", token);
            return query.getResultList();
        }
    }

    @Override
    public List<SmUserSession> getSessionsByType(Long accountId, String userSessionType) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmUserSession where accountId=:accountId and sessionType=:userSessionType order by dtCreated desc";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            query.setParameter("userSessionType", userSessionType);
            return query.getResultList();
        }
    }

    @Override
    public SmUserSession getSessionsByTypeAndRecourceId(Long accountId, String userSessionType, Long resourceId) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmUserSession where accountId=:accountId and sessionType=:userSessionType and resourceId=:resourceId order by dtCreated desc";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            query.setParameter("userSessionType", userSessionType);
            query.setParameter("resourceId", resourceId);
            query.setMaxResults(1);
            List<SmUserSession> result = query.getResultList();

            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public SmUserSession saveSession(SmUserSession smUserSession) {
        synchronized (Constants.class) {
            smUserSession.setIdUserSession((Long) sessionFactory.getCurrentSession().save(smUserSession));
            return smUserSession;
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteSession(Long accountId, Long resourceId, String userSessionType) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "delete FROM SmUserSession where accountId=:accountId and sessionType=:userSessionType and resourceId=:resourceId";
            Query query = session.createQuery(hql);
            query.setParameter("accountId", accountId);
            query.setParameter("userSessionType", userSessionType);
            query.setParameter("resourceId", resourceId);
            query.executeUpdate();
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public Collection<SmUserSession> saveSessions(Collection<SmUserSession> smUserSessions) {
        synchronized (Constants.class) {
            for (SmUserSession smUserSession : smUserSessions) {
                smUserSession.setIdUserSession((Long) sessionFactory.getCurrentSession().save(smUserSession));
            }
            return smUserSessions;
        }
    }
}
