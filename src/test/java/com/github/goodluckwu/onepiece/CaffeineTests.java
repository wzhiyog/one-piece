package com.github.goodluckwu.onepiece;

import java.time.Duration;
import java.time.LocalTime;
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
import com.google.common.testing.FakeTicker;
import org.junit.jupiter.api.Test;

public class CaffeineTests {
    @Test
    public void testCache() {
        Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            .maximumSize(10_000L)
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
            .maximumSize(10_000L)
            .expireAfterWrite(10L, TimeUnit.MINUTES)
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
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            .maximumSize(10_000L)
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
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            .maximumSize(10_000L)
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
            .maximumSize(10_000L)
            .expireAfterWrite(10L, TimeUnit.MINUTES)
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
            .maximumSize(10_000L)
            .expireAfterWrite(10L, TimeUnit.MINUTES)
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
            .maximumSize(1L)
            .build(key -> UUID.randomUUID().toString());
        cache.put("a", "1");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(1L);

        cache.put("b", "1");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        // key个数大于1就会驱逐，然后保留最近一个
        TimeUnit.SECONDS.sleep(5L);
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());
    }

    @Test
    public void testEvictByCapacityByWeight() throws InterruptedException {
        // 基于缓存内元素权重进行驱逐
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .maximumWeight(3L)
            .weigher((String key, String value) -> value.length())
            .build(key -> UUID.randomUUID().toString());
        cache.put("a", "111");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(1L);

        cache.put("b", "11");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(5L);
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());
    }

    @Test
    public void testEvictByExpireAfterAccess() throws InterruptedException {
        //  一个元素在上一次读写操作后一段时间之后，在指定的时间后没有被再次访问将会被认定为过期项。
        //  在当被缓存的元素时被绑定在一个session上时，当session因为不活跃而使元素过期的情况下，这是理想的选择。
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterAccess(5L, TimeUnit.SECONDS)
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(5L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
    }

    @Test
    public void testEvictByExpireAfterWrite() throws InterruptedException {
        //   一个元素将会在其创建或者最近一次被更新之后的一段时间后被认定为过期项。
        //   在对被缓存的元素的时效性存在要求的场景下，这是理想的选择。
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5L, TimeUnit.SECONDS)
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
    }

    @Test
    public void testEvictByExpireAfter() throws InterruptedException {
        // 基于不同的过期驱逐策略
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, String>() {
                public long expireAfterCreate(String key, String value, long currentTime) {
                    // 创建1秒后过期，可以看到这里必须要用纳秒
                    System.out.printf("expireAfterCreate: %s:%s:%s%n", key, value, currentTime);
                    return TimeUnit.SECONDS.toNanos(1);
                }
                public long expireAfterUpdate(String key, String value,
                                              long currentTime, long currentDuration) {
                    // 更新2秒后过期，可以看到这里必须要用纳秒
                    System.out.printf("expireAfterUpdate: %s:%s:%s:%s%n", key, value, currentTime, currentDuration);
                    return TimeUnit.SECONDS.toNanos(2);
                }
                public long expireAfterRead(String key, String value,
                                            long currentTime, long currentDuration) {
                    // 读3秒后过期，可以看到这里必须要用纳秒
                    System.out.printf("expireAfterRead: %s:%s:%s:%s%n", key, value, currentTime, currentDuration);
                    return TimeUnit.SECONDS.toNanos(3);
                }
            })
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        cache.put("aaa", "111");
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(6L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
    }

    @Test
    public void testRemovalListener() throws InterruptedException {
        //   一个元素将会在其创建或者最近一次被更新之后的一段时间后被认定为过期项。
        //   在对被缓存的元素的时效性存在要求的场景下，这是理想的选择。
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5L, TimeUnit.SECONDS)
            .evictionListener((key, value, cause) -> {
                System.out.printf("evictionListener: key = %s, value = %s, cause = %s%n", key, value, cause);
            })
            .removalListener((key, value, cause) -> {
                System.out.printf("removalListener: key = %s, value = %s, cause = %s%n", key, value, cause);
            })
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        cache.invalidate("aaa");
    }

    @Test
    public void testRefreshAfterWrite() throws InterruptedException {
        //   一个元素将会在其创建或者最近一次被更新之后的一段时间后被认定为过期项。
        //   在对被缓存的元素的时效性存在要求的场景下，这是理想的选择。
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5L, TimeUnit.SECONDS)
            .refreshAfterWrite(2L, TimeUnit.SECONDS)
            .evictionListener((key, value, cause) -> {
                System.out.printf("evictionListener: key = %s, value = %s, cause = %s%n", key, value, cause);
            })
            .removalListener((key, value, cause) -> {
                System.out.printf("removalListener: key = %s, value = %s, cause = %s%n", key, value, cause);
            })
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        System.out.println(LocalTime.now() + ": " + cache.get("bbb"));
        System.out.println(cache.asMap());
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        System.out.println(cache.asMap());
        fakeTicker.advance(10L, TimeUnit.SECONDS);
        System.out.println(cache.asMap());
    }

    @Test
    public void testPolicyMaximumSize() throws InterruptedException {
        // 基于缓存内的元素个数进行驱逐
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(1L)
            .build(key -> UUID.randomUUID().toString());
        cache.put("a", "1");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(1L);

        cache.put("b", "1");
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        // key个数大于1就会驱逐，然后保留最近一个
        TimeUnit.SECONDS.sleep(5L);
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());

        cache.policy().eviction().ifPresent(eviction -> {
            System.out.println("policy: " + eviction.getMaximum());
            System.out.println("policy: " + eviction.weightedSize());
            System.out.println("policy: " + cache.estimatedSize());
            eviction.setMaximum(2 * eviction.getMaximum());
        });

        // key个数大于1就会驱逐，然后保留最近一个
        cache.put("a", "1");
        cache.put("c", "1");
        System.out.println(cache.asMap());
        TimeUnit.SECONDS.sleep(5L);
        System.out.println(cache.estimatedSize());
        System.out.println(cache.asMap());
    }

    @Test
    public void testPolicyMaximumWeight() throws InterruptedException {
        // 基于缓存内的元素个数进行驱逐
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .maximumWeight(1L)
            .weigher((String key, String value) -> Integer.parseInt(value))
            .build(key -> UUID.randomUUID().toString());
        cache.put("a", "1");
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(1L);

        cache.put("b", "1");
        System.out.println(cache.asMap());

        TimeUnit.SECONDS.sleep(5L);
        System.out.println(cache.asMap());

        cache.policy().eviction().ifPresent(eviction -> {
            System.out.println("policy: " + eviction.getMaximum());
            System.out.println("policy: " + eviction.weightedSize());
            System.out.println("policy: " + cache.estimatedSize());
            eviction.setMaximum(2 * eviction.getMaximum());
        });

        cache.put("a", "1");
        cache.put("c", "1");
        System.out.println(cache.asMap());
        TimeUnit.SECONDS.sleep(5L);
        System.out.println(cache.asMap());
        cache.policy().eviction().ifPresent(eviction -> {
            System.out.println("policy: " + eviction.getMaximum());
            System.out.println("policy: " + eviction.weightedSize());
            System.out.println("policy: " + cache.estimatedSize());
        });
    }

    @Test
    public void testPolicyByExpireAfterAccess() throws InterruptedException {
        //  一个元素在上一次读写操作后一段时间之后，在指定的时间后没有被再次访问将会被认定为过期项。
        //  在当被缓存的元素时被绑定在一个session上时，当session因为不活跃而使元素过期的情况下，这是理想的选择。
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterAccess(5L, TimeUnit.SECONDS)
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(1L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(5L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(1L, TimeUnit.SECONDS);
        cache.policy().expireAfterAccess().ifPresent(expiration -> {
            System.out.println(cache.asMap());
            System.out.println(expiration.ageOf("aaa"));
            System.out.println(expiration.getExpiresAfter());
            expiration.setExpiresAfter(Duration.ofSeconds(1));
            System.out.println(expiration.getExpiresAfter());
        });
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(1L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        cache.policy().expireAfterAccess().ifPresent(expiration -> {
            System.out.println(expiration.ageOf("aaa"));
            System.out.println(expiration.getExpiresAfter());
            expiration.setExpiresAfter(Duration.ofSeconds(5));
            System.out.println(expiration.getExpiresAfter());
        });
    }

    @Test
    public void testPolicyByExpireAfterWrite() throws InterruptedException {
        //   一个元素将会在其创建或者最近一次被更新之后的一段时间后被认定为过期项。
        //   在对被缓存的元素的时效性存在要求的场景下，这是理想的选择。
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5L, TimeUnit.SECONDS)
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(3L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));

        fakeTicker.advance(3L, TimeUnit.SECONDS);
        cache.policy().expireAfterWrite().ifPresent(expiration -> {
            System.out.println(expiration.getExpiresAfter());
            System.out.println(expiration.ageOf("aaa"));
        });
    }

    @Test
    public void testPolicyByExpireAfter() throws InterruptedException {
        // 基于不同的过期驱逐策略
        FakeTicker fakeTicker = new FakeTicker();
        LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, String>() {
                public long expireAfterCreate(String key, String value, long currentTime) {
                    // 创建1秒后过期，可以看到这里必须要用纳秒
                    System.out.printf("expireAfterCreate: %s:%s:%s%n", key, value, currentTime);
                    return TimeUnit.SECONDS.toNanos(1);
                }
                public long expireAfterUpdate(String key, String value,
                                              long currentTime, long currentDuration) {
                    // 更新2秒后过期，可以看到这里必须要用纳秒
                    System.out.printf("expireAfterUpdate: %s:%s:%s:%s%n", key, value, currentTime, currentDuration);
                    return TimeUnit.SECONDS.toNanos(2);
                }
                public long expireAfterRead(String key, String value,
                                            long currentTime, long currentDuration) {
                    // 读3秒后过期，可以看到这里必须要用纳秒
                    System.out.printf("expireAfterRead: %s:%s:%s:%s%n", key, value, currentTime, currentDuration);
                    return TimeUnit.SECONDS.toNanos(3);
                }
            })
            .ticker(fakeTicker::read)
            .build(key -> UUID.randomUUID().toString());

        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        cache.put("aaa", "111");
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));
        fakeTicker.advance(6L, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + ": " + cache.get("aaa"));

        cache.policy().expireVariably().ifPresent(expiration -> {
            System.out.println(expiration.getExpiresAfter("aaa"));
            expiration.put("aaa", "111", Duration.ofSeconds(5));
            System.out.println(expiration.getExpiresAfter("aaa"));
        });
    }
}
