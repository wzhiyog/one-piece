package com.github.goodluckwu.onepiece.web.filter;

import com.github.goodluckwu.onepiece.trace.MDCTraceUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class WebTraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String spanId = request.getHeader(MDCTraceUtils.SPAN_ID);
            String traceId = request.getHeader(MDCTraceUtils.TRACE_ID);
            if (StringUtils.hasText(spanId)) {
                MDCTraceUtils.putSpanId(spanId);
            } else {
                MDCTraceUtils.addSpanId();
            }
            if (StringUtils.hasText(traceId)) {
                MDCTraceUtils.putTraceId(traceId);
            } else {
                MDCTraceUtils.addTraceId();
            }
            //把traceId放入response的header，为了方便有些人有这样的需求，从前端拿整条链路的traceId
            response.addHeader(MDCTraceUtils.SPAN_ID, MDCTraceUtils.getSpanId());
            response.addHeader(MDCTraceUtils.TRACE_ID, MDCTraceUtils.getTraceId());
            filterChain.doFilter(request, response);
        } finally {
            MDCTraceUtils.removeTraceId();
        }
    }
}
