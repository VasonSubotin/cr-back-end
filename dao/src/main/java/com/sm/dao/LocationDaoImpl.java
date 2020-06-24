package com.sm.dao;

import com.sm.model.SmLocation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Scope("prototype")
public class LocationDaoImpl implements LocationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<SmLocation> getAllLocations() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmLocation where deleted=0 or deleted is null";
        return session.createQuery(hql).getResultList();
    }

    @Override
    public List<SmLocation> getAllPersonalLocations(Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmLocation where (deleted=0 or deleted is null) and accountId=:accountId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    @Override
    public SmLocation getLocationByIdAndAccountId(Long id, Long accountId) {
        return sessionFactory.getCurrentSession().get(SmLocation.class, id);
    }

    @Override
    public SmLocation saveLocation(SmLocation smLocation, Long accountId) {
        smLocation.setIdLocation((Long) sessionFactory.getCurrentSession().save(smLocation));
        return smLocation;
    }

    @Override
    @Transactional
    public SmLocation deleteLocationById(Long id, Long accountId) {

        Query query = sessionFactory.getCurrentSession().createQuery("update SmLocation set deleted = 1 where idLocation = :id and accountId=:accountId");
        query.setParameter("id", id);
        query.setParameter("accountId", accountId);
        query.executeUpdate();
        return getLocationByIdAndAccountId(id, accountId);
    }
}
