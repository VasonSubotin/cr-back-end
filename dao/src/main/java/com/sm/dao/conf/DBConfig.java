package com.sm.dao.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@EnableTransactionManagement
@Configuration
public class DBConfig {

    @Value("${db.driverClassName:org.sqlite.JDBC}")
    private String driverClassName;

    @Value("${db.url:jdbc:sqlite:sm.sqlite}")//jdbc:sqlite:memory:myDb?cache=shared
    private String url;

    @Value("${db.user:#{null}}")
    private String user;

    @Value("${db.password:#{null}}")
    private String password;

    @Bean
    public DataSource dataSource() {
       // final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        final DriverManagerDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        ((SingleConnectionDataSource)dataSource).setSuppressClose(true);
        ((SingleConnectionDataSource) dataSource).setAutoCommit(false);
        return dataSource;
    }


    @Primary
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.sm.model");

        Properties prop = new Properties();
        prop.setProperty("hibernate.show_sql", "true");
        prop.setProperty("hibernate.dialect", SQLiteDialect.class.getName());
        sessionFactory.setHibernateProperties(prop);

        return sessionFactory;
    }

//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//        transactionManager.setSessionFactory(sessionFactory().getObject());
//        return transactionManager;
//    }
}
