package com.github.wzhiyog.ratelimiter.algorithm;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlidingWindowRateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(SlidingWindowRateLimiter.class);
    //分片窗口数
    private final int shardNum;
    //允许通过的请求数
    private final int maxRequestCount;
    //各个窗口内请求计数
    private final int[] shardRequestCount;
    //请求总数
    private int totalCount;
    //当前窗口下标
    private int shardId;
    //每个小窗口大小，毫秒
    private final long tinyWindowSize;
    //窗口右边界
    private long windowBorder;

    public SlidingWindowRateLimiter(long windowSize, int shardNum, int maxRequestCount) {
        this.shardNum = shardNum;
        this.maxRequestCount = maxRequestCount;
        this.shardRequestCount = new int[shardNum];
        this.tinyWindowSize = windowSize / shardNum;
        this.windowBorder = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        long currentTime = System.currentTimeMillis();
        // 当前时间已经超过窗口右边界，重置窗口
        if (windowBorder < currentTime) {
            logger.info("window reset");
            do {
                logger.info("window reset,current shardId:{}", shardId);
                shardId = (++shardId) % shardNum;
                logger.info("window reset,after shardId:{}", shardId);
                totalCount -= shardRequestCount[shardId];
                shardRequestCount[shardId] = 0;
                windowBorder += tinyWindowSize;
            } while (windowBorder < currentTime);
        }
        if (totalCount < maxRequestCount) {
            logger.info("tryAcquire success:{}, shardRequestCount:{}, totalCount:{}", shardId, Arrays.toString(shardRequestCount), totalCount);
            shardRequestCount[shardId]++;
            totalCount++;
            return true;
        } else {
            logger.info("tryAcquire fail");
            return false;
        }
    }

    public static void main(String[] args) {
        SlidingWindowRateLimiter slidingWindowRateLimiter = new SlidingWindowRateLimiter(3000, 3, 150);
        IntStream.range(0, 500).forEach(i -> {
            slidingWindowRateLimiter.tryAcquire();
            try {
                TimeUnit.MILLISECONDS.sleep(new Random().nextLong(100));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}