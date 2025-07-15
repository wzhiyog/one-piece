package com.github.goodluckwu.onepiece.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Selector {
    private LinkedBlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    private final Object lock = new Object();

    List<Event> select(){
        return select(0);
    }

    List<Event> select(long timeout){
        if(timeout > 0){
            if(eventQueue.isEmpty()){
                synchronized (lock){
                    if(eventQueue.isEmpty()){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        List<Event> events = new ArrayList<>();
        eventQueue.drainTo(events);
        return events;
    }

    public void addEvent(Event e){
        boolean offer = eventQueue.offer(e);
        if(offer){
            synchronized (lock){
                lock.notify();
            }
        }
    }
}
