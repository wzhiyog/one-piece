package com.github.goodluckwu.onepiece.trace;

import org.slf4j.MDC;

public class MDCTraceUtils {
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";

    public static final String PARENT_SPAN_ID = "parentSpanId";

    public static void addSpanId() {
        SpanIdGenerator.putSpanId("");
        MDC.put(SPAN_ID, getSpanId());
        MDC.put(PARENT_SPAN_ID, SpanIdGenerator.genParentSpanId());
    }

    public static void putSpanId(String spanId) {
        SpanIdGenerator.putSpanId(spanId);
        MDC.put(SPAN_ID, getSpanId());
        MDC.put(PARENT_SPAN_ID, SpanIdGenerator.genParentSpanId());
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
        MDC.remove(PARENT_SPAN_ID);
    }
}
