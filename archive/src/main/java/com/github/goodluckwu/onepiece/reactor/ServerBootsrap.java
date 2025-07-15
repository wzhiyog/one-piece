package com.github.goodluckwu.onepiece.reactor;

public class ServerBootsrap {
    public static void main(String[] args) {
        Server server = new Server(1234);
        new Thread(() -> {Acceptor.addNewConnection(new InputSource("testtttttttt", 1));}, "Client-1").start();
        new Thread(() -> {Acceptor.addNewConnection(new InputSource("testtttttttt", 2));}, "Client-2").start();
        new Thread(() -> {Acceptor.addNewConnection(new InputSource("testtttttttt", 3));}, "Client-3").start();
        server.start();
    }
}
