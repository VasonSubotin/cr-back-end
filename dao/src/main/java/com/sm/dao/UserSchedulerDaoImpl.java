package com.sm.dao;

import com.sm.model.SmUser;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserSchedulerDaoImpl implements UserSchedulerDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public SmUser getUserProfileById(long id) {

        return (SmUser) sessionFactory.openSession().get(SmUser.class, id);
    }
}
