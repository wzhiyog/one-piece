package com.github.goodluckwu.onepiece.validation;

import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(Integer id, String name) {
        return "hello, id: " + id + ", name: " + name;
//        return null;
    }

    @Override
    public Person cascade(Person father, Person mother) {
        return father;
    }
}
