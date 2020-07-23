package com.sm.client.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
public class GoogleLocationRestTemplateConfig extends RestTemplateConfig {

    @Bean(name = "googleLocationTemplate")
    public RestTemplate googleLocationTemplate() {
        return newTemplate();
    }

    protected Map<String, Object> getProperties() {
        // removing authorization we will add it separately depeneds on type of request
        Map<String, Object> props = super.getProperties();
        props.remove("restTemplate.username");
        props.remove("restTemplate.password");
        return props;
    }
}
