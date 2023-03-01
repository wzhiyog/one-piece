package com.github.goodluckwu.onepiece.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LifeCycleConfig {
    @Bean(destroyMethod = "des")
    public LifeCycle lifeCycle(){
        return new LifeCycle();
    }
}
