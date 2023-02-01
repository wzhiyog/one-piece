package com.github.goodluckwu.onepiece.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    Selector selector = new Selector();
    Dispatcher eventLooper = new Dispatcher(selector);

    Acceptor acceptor;

    Server(int port){
        this.acceptor = new Acceptor(this.selector, port);
    }

    public void start(){
        eventLooper.registEventHandler(EventType.ACCEPT, new AcceptEventHandler(selector));
        eventLooper.registEventHandler(EventType.READ, new ReadEventHandler());
        new Thread(acceptor, "Accept-" + acceptor.getPort()).start();
        log.info("Server started!");
        eventLooper.handleEvents();
    }
}
