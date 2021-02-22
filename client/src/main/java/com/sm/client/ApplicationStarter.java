package com.sm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
public class ApplicationStarter {
    private static Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

    public static void main(String[] args) {
      //  BasicConfigurator.configure();
        SpringApplication.run(ApplicationStarter.class, args);
    }

}
