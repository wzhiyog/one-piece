package com.github.goodluckwu.onepiece.reactor;

public abstract class EventHandler {
    private InputSource source;

    public InputSource getSource() {
        return source;
    }

    public void setSource(InputSource source) {
        this.source = source;
    }

    public abstract void handle(Event event);

    @Override
    public String toString() {
        return "EventHandler{" +
                "source=" + source +
                '}';
    }
}
