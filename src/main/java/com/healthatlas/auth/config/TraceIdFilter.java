package com.healthatlas.auth.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@Provider
@Priority(Priorities.USER)
public class TraceIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String TRACE_ID = "X-Trace-Id";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String traceId = getActiveTraceId();

        if (traceId == null || traceId.isBlank()) {
            traceId = containerRequestContext.getHeaderString(TRACE_ID);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }
        }

        MDC.put(TRACE_ID, traceId);
        containerRequestContext.setProperty(TRACE_ID, traceId);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext)
            throws IOException {
        String traceId = (String) containerRequestContext.getProperty(TRACE_ID);

        if (traceId != null) {
            containerResponseContext.getHeaders().add(TRACE_ID, traceId);
        }

        MDC.remove(TRACE_ID);
    }

    private String getActiveTraceId() {
        Span currentSpan = Span.current();
        SpanContext ctx = currentSpan.getSpanContext();
        if (ctx.isValid()) {
            return ctx.getTraceId();
        }
        return null;
    }
}
