package com.jy.zookeeper.locks;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(prefix = "jy.lock", name = "endpoint")
@EnableConfigurationProperties(LockProperties.class)
public class DistributeLockFactory {


    @Bean
    public LockService getLockService(LockProperties lockProperties) throws IOException {
        String endpoint = lockProperties.getEndpoint();
        int sessionTimeout = lockProperties.getSessionTimeout();
        return new LockService(endpoint, sessionTimeout);
    }
}
