package com.github.wzhiyog.ratelimiter.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(FixedWindowRateLimiter.class);
    //时间窗口大小，单位毫秒
    private final long windowSize;
    //允许通过的请求数
    private final int maxRequestCount;
    //当前窗口通过的请求数
    private AtomicInteger counter = new AtomicInteger(0);
    //窗口右边界
    private long windowBorder;

    public FixedWindowRateLimiter(long windowSize, int maxRequestCount) {
        this.windowSize = windowSize;
        this.maxRequestCount = maxRequestCount;
        this.windowBorder = System.currentTimeMillis() + windowSize;
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        // 当前时间已经超过窗口右边界，重置窗口
        if (windowBorder < currentTime) {
            logger.info("window reset");
            do {
                windowBorder += windowSize;
            } while (windowBorder < currentTime);
            counter = new AtomicInteger(0);
        }
        if (counter.intValue() < maxRequestCount) {
            counter.incrementAndGet();
            logger.info("tryAcquire success");
            return true;
        } else {
            logger.info("tryAcquire fail");
            return false;
        }
    }
}
