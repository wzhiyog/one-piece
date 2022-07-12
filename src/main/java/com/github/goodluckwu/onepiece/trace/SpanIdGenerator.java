package com.github.goodluckwu.onepiece.trace;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class SpanIdGenerator {
    private static final TransmittableThreadLocal<String> currentSpanIdTL = new TransmittableThreadLocal<>();

    private static final TransmittableThreadLocal<AtomicInteger> spanIndex = new TransmittableThreadLocal<>();

    public static void putSpanId(String spanId) {
        if (!StringUtils.hasText(spanId)) {
            spanId = "0";
        }
        currentSpanIdTL.set(spanId);
        spanIndex.set(new AtomicInteger(0));
    }

    public static String getSpanId() {
        return currentSpanIdTL.get();
    }

    public static void removeSpanId() {
        currentSpanIdTL.remove();
    }

    public static String generateNextSpanId() {
        String currentSpanId = currentSpanIdTL.get();
        int currentSpanIndex = spanIndex.get().incrementAndGet();
        return currentSpanId + "." + currentSpanIndex;
    }
}
