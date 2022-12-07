package com.github.goodluckwu.onepiece;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * <a>https://blog.csdn.net/weixin_42083242/article/details/85223512</a>
 */
public class ConcurrentHashMapTests {
    @Test
    public void test() {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        //caseA: dead loop
        map.computeIfAbsent("AA", key -> map.computeIfAbsent("BB", k -> "bb"));
    }

    @Test
    public void test2() throws InterruptedException {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        //caseB: block, but no dead loop
        Thread aa = new Thread(() -> {
            map.computeIfAbsent("AA", key -> waitAndGet());
            System.out.println("AA done");
        });
        aa.start();

        Thread bb = new Thread(() -> {
            map.computeIfAbsent("BB123", key -> {
                try {
                    TimeUnit.SECONDS.sleep(3);  //delay 1 second
                } catch (InterruptedException e) {
                }
                return "bb";
            });
            System.out.println("BB done");
        });
        bb.start();
        aa.join();
        bb.join();
    }

    private static String waitAndGet(){
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
        }
        return "AAA";
    }
}
