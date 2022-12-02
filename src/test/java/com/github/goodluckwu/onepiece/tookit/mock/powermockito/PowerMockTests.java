package com.github.goodluckwu.onepiece.tookit.mock.powermockito;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TestService.class)
public class PowerMockTests {
    @BeforeEach
    public void setUp(){
        PowerMockito.mockStatic(TestService.class);
        Object o = new Object();
        PowerMockito.when(TestService.newInstance2()).thenReturn(o);
        System.out.println("PowerMockTests.setUp, o= " + o);
    }

    @Test
    public void test(){
        System.out.println("PowerMockTests.test, o= " + TestService.newInstance2());
    }

    @Test
    public void test2(){
        System.out.println("PowerMockTests.test2, o= " + TestService.newInstance2());
    }
}

