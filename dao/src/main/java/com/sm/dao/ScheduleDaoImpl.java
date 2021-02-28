package com.sm.dao;

import com.sm.model.Constants;
import com.sm.model.SmScheduleType;
import com.sm.model.SmSchedules;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
@Component
public class ScheduleDaoImpl implements ScheduleDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public SmSchedules getSmSchedulesById(Long scheduleId, Long accountId) {

        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM SmSchedules where idSchedule=:scheduleId and accountId=:accountId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("scheduleId", scheduleId);
        List<SmSchedules> smSchedulesList = query.getResultList();
        return (smSchedulesList == null || smSchedulesList.isEmpty()) ? null : smSchedulesList.iterator().next();

    }


    @Override
    public SmSchedules getLastSmSchedulesByResourceIdAndType(Long resourceId, Long accountId, SmScheduleType type) {

        Session session = sessionFactory.getCurrentSession();

        String hql = "FROM SmSchedules where resourceId=:resourceId and accountId=:accountId and ( scheduleType=:type or :type is null) order by dtCreated desc";

        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        query.setParameter("type", type);
        query.setMaxResults(1);
        List<SmSchedules> smSchedulesList = query.getResultList();
        return (smSchedulesList == null || smSchedulesList.isEmpty()) ? null : smSchedulesList.iterator().next();

    }

    @Override
    public List<SmSchedules> getNoIntervalSmSchedulesByResourceIdAndDateBetween(Long resourceId, Long accountId, Date start, Date stop, SmScheduleType type) {

        Session session = sessionFactory.getCurrentSession();
        String hql = "select new com.sm.model.SmSchedules( s.idSchedule,s.accountId,s.locationId,s.resourceId,s.carbonImpact,s.carbonSavings,s.initEnergy,s.financeSavings,s.sessionId,s.policyId,s.dtCreated,s.dtStart,s.dtStop,s.scheduleType, s.endSoc, s.capacity) FROM SmSchedules s where (s.resourceId=:resourceId OR :resourceId is null) and s.accountId=:accountId and dtCreated between :start and :stop  and (s.scheduleType=:type  OR :type is null)  order by s.dtCreated desc"; //
        Query query = session.createQuery(hql);

        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        query.setParameter("start", start);
        query.setParameter("stop", stop);
        query.setParameter("type", type);

        return query.getResultList();

    }

    @Override
    public List<SmSchedules> getSmSchedulesByResourceIdAndDateBetweenAndType(Long resourceId, Long accountId, Date start, Date stop, SmScheduleType type) {

        Session session = sessionFactory.getCurrentSession();
        String hql = "select * FROM SmSchedules s where s.resourceId=:resourceId and s.accountId=:accountId and dtCreated between :start and :stop and (s.scheduleType=:type or :type is null) order by s.dtCreated desc"; //
        Query query = session.createQuery(hql);

        query.setParameter("accountId", accountId);
        query.setParameter("resourceId", resourceId);
        query.setParameter("start", start);
        query.setParameter("stop", stop);
        query.setParameter("type", type);
        return query.getResultList();

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @Override
    public SmSchedules saveSmSchedules(SmSchedules smSchedules) {

        Session session = sessionFactory.getCurrentSession();
        smSchedules.setSessionId((Long) sessionFactory.getCurrentSession().save(smSchedules));
        return smSchedules;

    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
