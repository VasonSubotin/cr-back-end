package com.sm.dao;

import com.sm.model.SmSchedules;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Component
public class ScheduleDaoImpl implements ScheduleDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public SmSchedules getSmSchedulesById(String scheduleId, String accountId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Schedules where scheduleId=:scheduleId and accountId=:accountId";
        Query query = session.createQuery(hql);
        query.setParameter("accountId", accountId);
        query.setParameter("scheduleId", scheduleId);
        List<SmSchedules> smSchedulesList = query.getResultList();
        return (smSchedulesList == null || smSchedulesList.isEmpty()) ? null : smSchedulesList.iterator().next();
    }


    @Transactional(readOnly = false)
    @Override
    public SmSchedules saveSmSchedules(SmSchedules smSchedules) {
        Session session = sessionFactory.getCurrentSession();
        smSchedules.setSessionId((Long) sessionFactory.getCurrentSession().save(smSchedules));
        return smSchedules;
    }
}
