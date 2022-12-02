package com.github.goodluckwu.onepiece.tookit.junit;

import org.junit.jupiter.api.*;

public class JunitLifeCycleTests extends BaseTests {
    private final Object instanceField = new Object();
    private static final Object INSTANCE_FIELD = new Object();

    @BeforeAll
    public static void childBeforeClass(){
        System.out.println("JunitLifeCycleTests.childBeforeClass");
    }

    @AfterAll
    public static void childAfterClass(){
        System.out.println("JunitLifeCycleTests.childAfterClass");
    }

    @BeforeEach
    public void childBefore(){
        System.out.println("JunitLifeCycleTests.childBefore");
    }

    @AfterEach
    public void childAfter(){
        System.out.println("JunitLifeCycleTests.childAfter");
    }

    @Test
    public void test1(){
        System.out.printf("JunitLifeCycleTests.test1, 实例变量: %s, 类变量: %s%n", instanceField, INSTANCE_FIELD);
    }

    @Test
    public void test2(){
        System.out.printf("JunitLifeCycleTests.test2, 实例变量: %s, 类变量: %s%n", instanceField, INSTANCE_FIELD);
    }
}
