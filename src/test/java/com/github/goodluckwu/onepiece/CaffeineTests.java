package com.github.goodluckwu.onepiece;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.jupiter.api.Test;

import static java.time.temporal.ChronoUnit.MILLIS;

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

    @Test
    public void testEvictByCapacityBySize() throws InterruptedException {
        // 基于缓存内的元素个数进行驱逐
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(1)
            .build(key -> UUID.randomUUID().toString());
        cache.put("a", "1");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(1L);

        cache.put("b", "1");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        // key个数大于1就会驱逐，然后保留最近一个
        TimeUnit.SECONDS.sleep(5);
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());
    }

    @Test
    public void testEvictByCapacityByWeight() throws InterruptedException {
        // 基于缓存内元素权重进行驱逐
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .maximumWeight(3)
            .weigher((String key, String value) -> value.length())
            .build(key -> UUID.randomUUID().toString());
        cache.put("a", "111");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(1L);

        cache.put("b", "11");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        // valuea.length() + valueb.length() > 2就会驱逐， 然后保留权重小的，驱逐最新的
        TimeUnit.SECONDS.sleep(5);
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());
    }

    @Test
    public void testEvictByCapacityByExpireAfterAccess() throws InterruptedException {
        //  一个元素在上一次读写操作后一段时间之后，在指定的时间后没有被再次访问将会被认定为过期项。
        //  在当被缓存的元素时被绑定在一个session上时，当session因为不活跃而使元素过期的情况下，这是理想的选择。
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        TimeUnit.SECONDS.sleep(3L);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        TimeUnit.SECONDS.sleep(3L);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        TimeUnit.SECONDS.sleep(5L);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
    }

    @Test
    public void testEvictByCapacityByExpireAfterWrite() throws InterruptedException {
        //   一个元素将会在其创建或者最近一次被更新之后的一段时间后被认定为过期项。
        //   在对被缓存的元素的时效性存在要求的场景下，这是理想的选择。
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        TimeUnit.SECONDS.sleep(3L);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        TimeUnit.SECONDS.sleep(3L);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
    }

    @Test
    public void testEvictByCapacityByExpireAfter() throws InterruptedException {
        // 基于不同的过期驱逐策略
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, String>() {
                public long expireAfterCreate(String key, String value, long currentTime) {
                    // Use wall clock time, rather than nanotime, if from an external resource
                    long seconds = LocalDateTime.now().plusHours(5)
                        .minus(System.currentTimeMillis(), MILLIS)
                        .toEpochSecond(ZoneOffset.UTC);
                    return TimeUnit.SECONDS.toNanos(seconds);
                }
                public long expireAfterUpdate(String key, String value,
                                              long currentTime, long currentDuration) {
                    return currentDuration;
                }
                public long expireAfterRead(String key, String value,
                                            long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        TimeUnit.SECONDS.sleep(6L);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
    }
}
