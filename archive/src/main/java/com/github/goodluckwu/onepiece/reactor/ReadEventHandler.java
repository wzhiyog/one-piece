package com.github.goodluckwu.onepiece.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadEventHandler extends EventHandler{
    private static final Logger log = LoggerFactory.getLogger(ReadEventHandler.class);
    @Override
    public void handle(Event event) {
        if(event.getType() == EventType.READ){
            log.info("read a event: {}", event);
        }
    }
}
