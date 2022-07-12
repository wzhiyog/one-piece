package com.github.goodluckwu.onepiece.trace;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.concurrent.atomic.AtomicInteger;

public class TraceIdGenerator {

    private static String IP_16 = "ffffffff";
    private static final AtomicInteger count = new AtomicInteger(1000);

    private static final TransmittableThreadLocal<String> currentTraceIdTL = new TransmittableThreadLocal<>();

    static {
        try {
            String ipAddress = TracerUtils.getInetAddress();
            if (ipAddress != null) {
                IP_16 = getIP_16(ipAddress);
            }
        } catch (Throwable e) {
            /*
             * empty catch block
             */
        }
    }

    private static String getTraceId(String ip, long timestamp, int nextId) {
        return ip + timestamp + nextId + TracerUtils.getPID();
    }

    public static String generate() {
        return getTraceId(IP_16, System.currentTimeMillis(), getNextId());
    }

    private static String getIP_16(String ip) {
        String[] ips = ip.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String column : ips) {
            String hex = Integer.toHexString(Integer.parseInt(column));
            if (hex.length() == 1) {
                sb.append('0').append(hex);
            } else {
                sb.append(hex);
            }

        }
        return sb.toString();
    }

    private static int getNextId() {
        for (; ; ) {
            int current = count.get();
            int next = (current > 9000) ? 1000 : current + 1;
            if (count.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    public static void putTraceId(String traceId) {
        currentTraceIdTL.set(traceId);
    }

    public static String getTraceId() {
        return currentTraceIdTL.get();
    }

    public static void removeTraceId() {
        currentTraceIdTL.remove();
    }
}
