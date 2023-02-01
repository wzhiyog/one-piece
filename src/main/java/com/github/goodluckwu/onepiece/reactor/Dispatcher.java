package com.github.goodluckwu.onepiece.reactor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher {
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);
    private Map<EventType, EventHandler> eventHandlerMap = Collections.synchronizedMap(new EnumMap<>(EventType.class));

    Selector selector;

    Dispatcher(Selector selector){
        this.selector = selector;
    }

    public void registEventHandler(EventType eventType, EventHandler eventHandler){
        eventHandlerMap.put(eventType, eventHandler);
    }

    public void removeEventHandler(EventType eventType){
        eventHandlerMap.remove(eventType);
    }

    public void handleEvents(){
        dispatch();
    }

    private void dispatch() {
        while (true){
            List<Event> events = selector.select();
            for (Event event : events) {
                log.info("dispatcher: {}", event);
                EventHandler eventHandler = eventHandlerMap.get(event.getType());
                eventHandler.handle(event);
            }
        }
    }
}
