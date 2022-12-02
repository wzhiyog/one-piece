package com.github.goodluckwu.onepiece.tookit.junit;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTests {
    @BeforeAll
    public static void baseBeforeClass(){
        System.out.println("BaseTests.baseBeforeClass");
    }

    @AfterAll
    public static void baseAfterClass(){
        System.out.println("BaseTests.baseAfterClass");
    }

    @BeforeEach
    public void baseBefore(){
        System.out.println("BaseTests.baseBefore");
    }

    @AfterEach
    public void baseAfter(){
        System.out.println("BaseTests.baseAfter");
    }
}
