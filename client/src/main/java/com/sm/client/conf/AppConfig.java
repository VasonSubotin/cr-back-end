package com.sm.client.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.sm.client","com.sm.dao"})
public class AppConfig {
}
