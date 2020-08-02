package com.sm.client.conf;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
@ComponentScan(basePackages = {"com.sm.client","com.sm.dao"})
public class AppConfig {
}
