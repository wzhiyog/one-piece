package com.github.goodluckwu.onepiece;

import com.github.benmanes.caffeine.cache.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CaffeineTests {
    @Test
    public void testCache() {
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
        String key = "key";
        // 查找一个缓存元素， 没有查找到的时候返回null
        String value = cache.getIfPresent(key);
        System.out.println(value);
        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        value = cache.get(key, k -> k + ": value");
        System.out.println(value);
        // 添加或者更新一个缓存元素
        cache.put(key, value);
        System.out.println(cache.getIfPresent(key));
        // 移除一个缓存元素
        cache.invalidate(key);
    }

    @Test
    public void testLoadingCache() {
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(key -> UUID.randomUUID().toString());

        String key = "key";
// 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        String value = cache.get(key);
        System.out.println(value);
// 批量查找缓存，如果缓存不存在则生成缓存元素
        Map<String, String> all = cache.getAll(List.of("key", "key2"));
        System.out.println(all);
    }

    @Test
    public void testAsyncCache() throws ExecutionException, InterruptedException {
        AsyncCache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .buildAsync();

        String key = "key";
// 查找一个缓存元素， 没有查找到的时候返回null
        CompletableFuture<String> value = cache.getIfPresent(key);
        System.out.println(value);
// 查找缓存元素，如果不存在，则异步生成
        value = cache.get(key, k -> {
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName());
            return k + ": value";
        });
        System.out.println(value.get());
        System.out.println(cache.synchronous().getIfPresent(key));
// 添加或者更新一个缓存元素
        cache.put(key, value);
// 移除一个缓存元素
        cache.synchronous().invalidate(key);
    }

    @Test
    public void testAsyncCache2() throws ExecutionException, InterruptedException {
        AsyncCache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .executor(Executors.newCachedThreadPool())
                .buildAsync();

        String key = "key";
// 查找一个缓存元素， 没有查找到的时候返回null
        CompletableFuture<String> value = cache.getIfPresent(key);
        System.out.println(value);
// 查找缓存元素，如果不存在，则异步生成
        value = cache.get(key, k -> {
            try {
                TimeUnit.SECONDS.sleep(5L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName());
            return k + ": value";
        });
        System.out.println(value.get());
        System.out.println(cache.synchronous().getIfPresent(key));
// 添加或者更新一个缓存元素
        cache.put(key, value);
// 移除一个缓存元素
        cache.synchronous().invalidate(key);
    }

    @Test
    public void testAsyncLoadingCache() throws ExecutionException, InterruptedException {
        AsyncLoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .executor(Executors.newCachedThreadPool())
                // 你可以选择: 去异步的封装一段同步操作来生成缓存元素
                .buildAsync(key -> {
                    TimeUnit.SECONDS.sleep(5L);
                    System.out.println(Thread.currentThread().getName());
                    return UUID.randomUUID().toString();
                });
        System.out.println("========================");
        String key = "key";
// 查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<String> value = cache.get(key);
        System.out.println(value.get());
// 批量查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<Map<String, String>> all = cache.getAll(List.of("key", "key2"));
        System.out.println(all.get());
    }

    @Test
    public void testAsyncLoadingCache2() throws ExecutionException, InterruptedException {
        AsyncLoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .executor(Executors.newCachedThreadPool())
        // 你也可以选择: 构建一个异步缓存元素操作并返回一个future
    .buildAsync((key, executor) -> CompletableFuture.supplyAsync(() -> {
        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName());
        return UUID.randomUUID().toString();
    }, executor));
        System.out.println("========================");
        String key = "key";
// 查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<String> value = cache.get(key);
        System.out.println(value.get());
// 批量查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<Map<String, String>> all = cache.getAll(List.of("key", "key2"));
        System.out.println(all.get());
    }
}
