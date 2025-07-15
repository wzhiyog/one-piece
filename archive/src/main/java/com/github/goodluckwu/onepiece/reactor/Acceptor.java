package com.github.goodluckwu.onepiece.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Acceptor implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Acceptor.class);
    private final Selector selector;

    private final int port;
    private static final BlockingQueue<InputSource> sourceQueue = new LinkedBlockingQueue<>();

    public Acceptor(Selector selector, int port) {
        this.selector = selector;
        this.port = port;
    }

    public static void addNewConnection(InputSource source) {
        log.info("receive a message: {}", source);
        Acceptor.sourceQueue.offer(source);
    }

    public int getPort(){
        return port;
    }

    @Override
    public void run() {
        while (true){
            try {
                InputSource source = Acceptor.sourceQueue.take();
                log.info("Acceptor: {}", source);
                Event acceptEvent = new Event();
                acceptEvent.setSource(source);
                acceptEvent.setType(EventType.ACCEPT);
                selector.addEvent(acceptEvent);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
