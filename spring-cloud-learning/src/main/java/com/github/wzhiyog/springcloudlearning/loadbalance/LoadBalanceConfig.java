package com.github.wzhiyog.springcloudlearning.loadbalance;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class LoadBalanceConfig {
    @LoadBalanced
    @Bean
    RestClient.Builder restclientBuilder() {
        return RestClient.builder();
    }
}
