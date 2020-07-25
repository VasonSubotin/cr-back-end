package com.sm.dao.cache;

import com.sm.model.cache.Coordinates;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@Transactional(readOnly = true)
@Component
public class CoordinatesCacheDaoImpl implements CoordinatesCacheDao {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatesCacheDaoImpl.class);
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS CoordinatesCache(" +
                            "ID INTEGER Primary key AUTOINCREMENT," +
                            "V_ADDRESS varchar(512) not null, " +
                            "F_LATITUDE numeric(7,7) not null," +
                            "F_LONGITUDE numeric(7,7) not null)"
            );
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void saveCoordinates(Coordinates coordinates) {
        sessionFactory.getCurrentSession().save(coordinates);
    }

    @Override
    public Coordinates loadCoordinates(String address) {

        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Coordinates where  address=:address";
        Query query = session.createQuery(hql);
        query.setParameter("address", address);

        List<Coordinates> results = query.getResultList();
        if (results == null || results.isEmpty()) {
            return null;
        }
        return results.iterator().next();
    }

}
