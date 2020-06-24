package com.sm.dao;

import com.sm.model.SmResource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class RecourcesDaoImpl implements ResourcesDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmResource> getAllResourceByAccountId(Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmResource where (deleted=0 or deleted is null) and and accountId=:accountId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    @Override
    public SmResource getResourceByIdAndAccountId(Long id, Long accountId) {
        return sessionFactory.getCurrentSession().get(SmResource.class, id);
    }

    @Override
    public SmResource saveResource(SmResource smResource, Long accountId) {
        smResource.setAccountId(accountId);
        smResource.setIdResource((Long) sessionFactory.getCurrentSession().save(smResource));
        return smResource;
    }

    @Override
    @Transactional
    public SmResource deleteResourceByIdAndAccountId(Long id, Long accountId) {
        //sessionFactory.getCurrentSession().update("UPDATE SmResource set SmResource.deleted");
        Query query = sessionFactory.getCurrentSession().createQuery("update SmResource set deleted = 1 where idResource = :id and accountId=:accountId");
        query.setParameter("id", id);
        query.setParameter("accountId", accountId);
        query.executeUpdate();
        return getResourceByIdAndAccountId(id, accountId);
    }
}
