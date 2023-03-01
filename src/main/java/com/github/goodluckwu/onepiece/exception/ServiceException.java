package com.github.goodluckwu.onepiece.exception;

public class ServiceException extends RuntimeException{
    private String code;
    private String description;
    private Object data;
    public ServiceException(String message){
        super(message);
    }

    public ServiceException(String message, Throwable cause){
        super(message, cause);
    }

    public ServiceException(Throwable cause){
        super(cause);
    }
}
