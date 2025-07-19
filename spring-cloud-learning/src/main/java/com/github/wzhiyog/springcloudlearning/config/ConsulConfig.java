package com.github.wzhiyog.springcloudlearning.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@ConfigurationProperties
public class ConsulConfig implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsulConfig.class);

    private String key;

    @Autowired
    private Environment environment;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "ConsulConfig{" +
                "key='" + key + '\'' +
                '}';
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> LOGGER.atInfo().setMessage("from consul config, value={}, this: {}").addArgument(environment.getProperty("key")).addArgument(this).log(), 0, 5, TimeUnit.SECONDS);
    }
}
