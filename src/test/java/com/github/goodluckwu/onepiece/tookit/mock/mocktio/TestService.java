package com.github.goodluckwu.onepiece.tookit.mock.mocktio;

public class TestService {
    public String greeting(String name){
        System.out.println("TestService.greeting");
        return "hello, " + name;
    }

    public String greeting2(String name){
        System.out.println("TestService.greeting2");
        return "hello, " + name;
    }

    public void sayHello(String name){
        System.out.println("hello, " + name);
    }

    public Object newInstance(){
        System.out.println("TestService.newInstance");
        return new Object();
    }

    public static Object newInstance2(){
        System.out.println("TestService.newInstance2");
        return new Object();
    }
}
