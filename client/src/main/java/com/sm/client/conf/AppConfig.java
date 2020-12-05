package com.sm.client.conf;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableEncryptableProperties
@ComponentScan(basePackages = {"com.sm.client", "com.sm.dao", "com.sm.ocpp"})
@EnableScheduling
public class AppConfig {

    @Value("${corePoolSize:8}")
    private int corePoolSize = 8;

    @Value("${maxPoolSize:16}")
    private int maxPoolSize = 16;

    @Value("${queueSize:1000}")
    private int queueSize = 1000;

    @Bean(name = "encryptConfig")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(System.getenv("jasypt_encryptor_password"));
        config.setAlgorithm(System.getenv("jasypt_encryptor_algorithm"));
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setIvGenerator(new RandomIvGenerator());
        // config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    @Bean(name = "commonThreadPool")
    TaskExecutor commonTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueSize);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setThreadNamePrefix("Common-");
        // threadPoolTaskExecutor.setKeepAliveSeconds(120);
        return threadPoolTaskExecutor;
    }
}
