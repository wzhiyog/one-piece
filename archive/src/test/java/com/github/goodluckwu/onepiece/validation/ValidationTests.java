package com.github.goodluckwu.onepiece.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ValidationTests {
    @Autowired
    HelloService helloService;

    @Test
    void test(){
        String hello = helloService.hello(11, "张三");
        Assertions.assertThat(hello).isNotBlank().startsWith("hello").endsWith("张三");
        System.out.println(hello);
    }

    @Test
    void test2(){
        Person father = new Person();
        father.setName("小头爸爸");
        father.setAge(10);
        Person.InnerChild child = new Person.InnerChild();
        child.setName("大头儿子");
        child.setAge(1);
        father.setChild(child);
        Person cascade = helloService.cascade(father, new Person());
        System.out.println(cascade);
    }
}
