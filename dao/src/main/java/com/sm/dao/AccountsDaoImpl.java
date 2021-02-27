package com.sm.dao;

import com.sm.model.Constants;
import com.sm.model.SmAccount;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

//@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
@Component
public class AccountsDaoImpl implements AccountsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public List<SmAccount> getAllAccounts() {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmAccount where deleted=0 or deleted is null";
            return session.createQuery(hql).getResultList();
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public SmAccount getAccountByLogin(String login) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmAccount where (deleted=0 or deleted is null) and login=:login";
            Query query = session.createQuery(hql);
            query.setParameter("login", login);
            List ret = query.getResultList();
            return ret.isEmpty() ? null : (SmAccount) ret.get(0);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public SmAccount getAccountByLoginByAuthorizationType(String login, String authorizationType) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            String hql = "FROM SmAccount where (deleted=0 or deleted is null) and login=:login and accountType:accountType";
            Query query = session.createQuery(hql);
            query.setParameter("login", login);
            query.setParameter("accountType", authorizationType);
            List ret = query.getResultList();
            return ret.isEmpty() ? null : (SmAccount) ret.get(0);
        }
    }


    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public SmAccount getAccountById(Long id) {
        synchronized (Constants.class) {
            return sessionFactory.getCurrentSession().get(SmAccount.class, id);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public SmAccount saveAccount(SmAccount smAccount) {
        synchronized (Constants.class) {
            smAccount.setIdAccount((Long) sessionFactory.getCurrentSession().save(smAccount));
            return smAccount;
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public SmAccount updateAccount(SmAccount smAccount) {
        synchronized (Constants.class) {
            sessionFactory.getCurrentSession().merge(smAccount);
            return smAccount;
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public SmAccount deleteAccountById(Long id) {
        synchronized (Constants.class) {
            Query query = sessionFactory.getCurrentSession().createQuery("update SmAccount set deleted = 1 where idAccount = :id");
            query.setParameter("id", id);
            query.executeUpdate();
            return getAccountById(id);
        }
    }
}
