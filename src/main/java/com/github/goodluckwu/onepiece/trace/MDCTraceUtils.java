package com.github.goodluckwu.onepiece.trace;

import org.slf4j.MDC;

public class MDCTraceUtils {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";

    public static void addSpanId() {
        SpanIdGenerator.putSpanId("");
        MDC.put(SPAN_ID, getSpanId());
    }

    public static void putSpanId(String spanId) {
        SpanIdGenerator.putSpanId(spanId);
        MDC.put(SPAN_ID, getSpanId());
    }

    public static void putTraceId(String traceId) {
        TraceIdGenerator.putTraceId(traceId);
        MDC.put(TRACE_ID, getTraceId());
    }

    public static void addTraceId() {
        TraceIdGenerator.putTraceId(TraceIdGenerator.generate());
        MDC.put(TRACE_ID, getTraceId());
    }

    public static String getSpanId() {
        return SpanIdGenerator.getSpanId();
    }

    public static String getTraceId() {
        return TraceIdGenerator.getTraceId();
    }

    public static void removeTraceId() {
        TraceIdGenerator.removeTraceId();
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
    }
}
