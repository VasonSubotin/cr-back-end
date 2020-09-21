package com.sm.dao;

import com.sm.model.Constants;
import com.sm.model.SmSchedules;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
@Component
public class ScheduleDaoImpl implements ScheduleDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public SmSchedules getSmSchedulesById(String scheduleId, String accountId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSchedules where scheduleId=:scheduleId and accountId=:accountId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("scheduleId", scheduleId);
        List<SmSchedules> smSchedulesList = query.getResultList();
        return (smSchedulesList == null || smSchedulesList.isEmpty()) ? null : smSchedulesList.iterator().next();
    }


    @Override
    public SmSchedules getLastSmSchedulesByResourceId(Long resourceId, Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSchedules where resourceId=:resourceId and accountId=:accountId order by dtCreated desc";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        query.setMaxResults(1);
        List<SmSchedules> smSchedulesList = query.getResultList();
        return (smSchedulesList == null || smSchedulesList.isEmpty()) ? null : smSchedulesList.iterator().next();
    }

    @Override
    public List<SmSchedules> getLastSmSchedulesByResourceId(Long resourceId, Long accountId, Date start, Date stop) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "select new com.sm.model.SmSchedules( s.idSchedule,s.accountId,s.locationId,s.resourceId,s.carbonImpact,s.carbonSavings,s.initEnergy,s.financeSavings,s.sessionId,s.policyId,s.dtCreated,s.dtStart,s.dtStop) FROM SmSchedules s where s.resourceId=:resourceId and s.accountId=:accountId and dtCreated between :start and :stop order by s.dtCreated desc"; //
        Query query = session.createQuery(hql);

        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        query.setParameter("start", start);
        query.setParameter("stop", stop);

        return query.getResultList();
    }

    @Transactional(readOnly = false)
    @Override
    public SmSchedules saveSmSchedules(SmSchedules smSchedules) {
        synchronized (Constants.class) {
            Session session = sessionFactory.getCurrentSession();
            smSchedules.setSessionId((Long) sessionFactory.getCurrentSession().save(smSchedules));
            return smSchedules;
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
