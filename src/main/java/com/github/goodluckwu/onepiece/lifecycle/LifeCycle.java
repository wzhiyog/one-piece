package com.github.goodluckwu.onepiece.lifecycle;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;

public class LifeCycle implements DisposableBean {
    @Override
    public void destroy() {
        System.out.printf("%s%n", "destory");
    }

    @EventListener
    public void close(ContextClosedEvent close){
        System.out.printf("%s%n", "close");
    }

    @EventListener
    public void stop(ContextStoppedEvent stop){
        System.out.printf("%s%n", "stop");
    }

    @PreDestroy
    public void preDestory(){
        System.out.printf("%s%n", "preDestory");
    }

    public void des() {
        System.out.printf("des");
    }
}
