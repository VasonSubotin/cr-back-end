package com.sm.client.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
public class EcoRestTemplateConfig extends RestTemplateConfig {

    @Bean(name = "ecoTemplate")
    public RestTemplate ecoTemplate() {
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
