package com.github.goodluckwu.onepiece.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcceptEventHandler extends EventHandler{
    private static final Logger log = LoggerFactory.getLogger(AcceptEventHandler.class);
    private final Selector selector;

    public AcceptEventHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void handle(Event event) {
        if(event.getType() == EventType.ACCEPT){
            log.info("accept a event: {}", event);
            Event readEvent = new Event();
            readEvent.setSource(event.getSource());
            readEvent.setType(EventType.READ);
            selector.addEvent(readEvent);
        }
    }
}
