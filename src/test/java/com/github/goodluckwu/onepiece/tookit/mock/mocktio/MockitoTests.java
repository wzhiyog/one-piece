package com.github.goodluckwu.onepiece.tookit.mock.mocktio;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTests {
    @Mock
    TestService mockTestService;

    @Spy
    TestService spyTestService;

    @Test
    public void test_when_then_return(){
        when(mockTestService.greeting(anyString())).thenReturn("setUp");
        String test = mockTestService.greeting("test");
        System.out.println(test);
    }

    @Test
    public void test_doreturn_when(){
        doReturn("setUp").when(mockTestService).greeting2(anyString());
        String test = mockTestService.greeting2("test");
        System.out.println(test);
    }

    @Test
    public void test_when_then_return_spy(){
        when(spyTestService.greeting(anyString())).thenReturn("setUp");
        String test = spyTestService.greeting("test");
        System.out.println(test);
    }

    @Test
    public void test_doreturn_when_spy(){
        doReturn("setUp").when(spyTestService).greeting2(anyString());
        String test = spyTestService.greeting2("test");
        System.out.println(test);
    }

    @Test
    public void test2_when_then_return(){
        when(mockTestService.greeting(anyString())).thenReturn("test2_when_then_return");
        when(mockTestService.greeting(anyString())).thenReturn("test2_when_then_return2");
        String test = mockTestService.greeting("test2");
        String test2 = mockTestService.greeting("test2");
        System.out.println(test);
        System.out.println(test2);
    }

    @Test
    public void test2_doreturn_when(){
        doReturn("test2_when_then_return").when(mockTestService).greeting(anyString());
        doReturn("test2_when_then_return2").when(mockTestService).greeting(anyString());
        String test = mockTestService.greeting("test2");
        String test2 = mockTestService.greeting("test2");
        System.out.println(test);
        System.out.println(test2);

        doNothing().when(mockTestService).sayHello(anyString());
        mockTestService.sayHello("test");
    }

    @Test
    public void test3_when_then_return(){
        when(mockTestService.greeting(anyString())).thenReturn("test3_when_then_return", "test3_when_then_return2");
        String test = mockTestService.greeting("test3");
        String test2 = mockTestService.greeting("test3");
        System.out.println(test);
        System.out.println(test2);
    }

    @Test
    public void test3_doreturn_when(){
        doReturn("test3_when_then_return", "test3_when_then_return2").when(mockTestService).greeting(anyString());
        String test = mockTestService.greeting("test3");
        String test2 = mockTestService.greeting("test3");
        System.out.println(test);
        System.out.println(test2);
    }

    @Test
    public void test4_when_then_return(){
        int count = 0;
        when(mockTestService.greeting(anyString())).thenReturn(String.valueOf(++count));
        IntStream.range(0, 2).forEach(i -> {
            System.out.println("count stub once: " + mockTestService.greeting("test4"));
        });

        AtomicInteger count2 = new AtomicInteger();
        IntStream.range(0, 2).forEach(i -> {
            when(mockTestService.greeting(anyString())).thenReturn(String.valueOf(count2.incrementAndGet()));
            System.out.println("count2 stub everytime: " + mockTestService.greeting("test4"));
        });

        AtomicInteger count3 = new AtomicInteger();
        when(mockTestService.greeting(anyString())).thenAnswer(invocation -> String.valueOf(count3.incrementAndGet()));
        IntStream.range(0, 2).forEach(i -> {
            System.out.println("count3 stub everytime: " + mockTestService.greeting("test4"));
        });
    }

    @Test
    public void test4_doreturn_when(){
        int count = 0;
        doReturn(String.valueOf(++count)).when(mockTestService).greeting(anyString());
        IntStream.range(0, 2).forEach(i -> {
            System.out.println("count stub once: " + mockTestService.greeting("test4"));
        });

        AtomicInteger count2 = new AtomicInteger();
        IntStream.range(0, 2).forEach(i -> {
            doReturn(String.valueOf(count2.incrementAndGet())).when(mockTestService).greeting(anyString());
            System.out.println("count2 stub everytime: " + mockTestService.greeting("test4"));
        });

        AtomicInteger count3 = new AtomicInteger();
        doAnswer(invocation -> String.valueOf(count3.incrementAndGet())).when(mockTestService).greeting(anyString());
        IntStream.range(0, 2).forEach(i -> {
            System.out.println("count3 stub everytime: " + mockTestService.greeting("test4"));
        });
    }

    @Test
    public void test_verify(){
        when(mockTestService.greeting(anyString())).thenReturn("setUp");
        String test = mockTestService.greeting("test");
        System.out.println("result: " + test);
        // 捕获参数
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        // 验证
        verify(mockTestService, only()).greeting(argumentCaptor.capture());
        String capture = argumentCaptor.getValue();
        System.out.println("args: " + capture);
    }
}

