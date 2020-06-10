package com.sm.client.conf;


import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class RestTemplateConfig {

    private static Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Value("${rt.timeout:30000}")
    private Integer defaultTimeout;

    @Value("${rt.connect.timeout:30000}")
    private Integer defaultConnectTimeout;

    @Value("${rt.username:#{null}}")
    private String defaultUsername;

    @Value("${rt.password:#{null}}")
    private String defaultPassword;

    @Value("${rt.keepAlive.timeout.seconds:300}")
    private Integer defaultKeepAliveTimeout;

    @Value("${rt.max.connections.total:32}")
    private Integer defaultMaxConnectionsTotal;

    @Value("${rt.max.connections.per.endpoint.default:5}")
    private Integer defaultMaxConnectionsPerEndpointDefault;

    @Value("${rt.connect.request.timeout:30000}")
    private Integer defaultConnectRequestTimeout;

    @Autowired
    private Environment environment;


    protected RestTemplate newTemplate() {
        Map<String, Object> props = getProperties();

        String username = (String) props.get("restTemplate.username");
        String password = (String) props.get("restTemplate.password");

        Integer maxConnectionsTotal = getInteger(props, "restTemplate.max.connections.total");
        Integer maxConnectionsPerEndpointDefault = getInteger(props, "restTemplate.max.connections.per.endpoint.default");
        Integer timeout = getInteger(props, "restTemplate.timeout");
        Integer connectTimeout = getInteger(props, "restTemplate.connect.timeout");
        Integer connectRequestTimeout = getInteger(props, "restTemplate.requestTimeout.timeout");

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(maxConnectionsTotal);
        cm.setDefaultMaxPerRoute(maxConnectionsPerEndpointDefault);

        CloseableHttpClient httpClient = setCustomBuildSettings(HttpClients.custom()
                .setConnectionManager(cm).setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setKeepAliveStrategy(getStrategy(props))).build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setReadTimeout(timeout);
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setConnectionRequestTimeout(connectRequestTimeout);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        if (username != null && !username.isEmpty()) {
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        }
        restTemplate.setMessageConverters(new ArrayList<>(Arrays.asList(new MappingJackson2HttpMessageConverter())));

        return restTemplate;
    }

    protected HttpClientBuilder setCustomBuildSettings(HttpClientBuilder builder) {
        return builder;
    }

    protected ConnectionKeepAliveStrategy getStrategy(Map<String, Object> properties) {

        int keepAliveTimeout = getInteger(properties, "restTemplate.keepAlive.timeout.seconds");
        ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase
                            ("timeout")) {
                        logger.info("Setting timeout for keepAlive {} sec - due to server side setting", value);
                        return Long.parseLong(value) * 1000;
                    }
                }
                logger.info("Setting timeout for keepAlive {} sec - from client config", keepAliveTimeout);
                return keepAliveTimeout * 1000;
            }
        };

        return keepAliveStrategy;
    }


    protected int getInteger(Map<String, Object> mapValue, String name) {
        Object value = mapValue.get(name);
        if (value == null) {
            throw new InvalidParameterException("Parameter " + name + " can't be null!");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return Double.valueOf(value.toString()).intValue();
    }

    protected Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put("restTemplate.keepAlive.timeout.seconds", defaultKeepAliveTimeout);
        props.put("restTemplate.username", defaultUsername);
        props.put("restTemplate.password", defaultPassword);

        props.put("restTemplate.max.connections.total", defaultMaxConnectionsTotal);
        props.put("restTemplate.max.connections.per.endpoint.default", defaultMaxConnectionsPerEndpointDefault);
        props.put("restTemplate.timeout", defaultTimeout);

        props.put("restTemplate.connect.timeout", defaultConnectTimeout);
        props.put("restTemplate.requestTimeout.timeout", defaultConnectRequestTimeout);

        return props;
    }

}

