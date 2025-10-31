package com.healthatlas.auth.exception;

import com.healthatlas.auth.config.TraceIdFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import java.time.Instant;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(String.valueOf(GlobalExceptionMapper.class));

    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(Exception e) {
        String traceId = (String) requestContext.getProperty(TraceIdFilter.TRACE_ID);
        LOG.error("Unhandled exception occurred", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .entity(new ErrorResponse(
                        e.getClass().getSimpleName(),
                        "An unexpected error occurred. Please contact support.",
                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                        Instant.now(),
                        traceId
                ))
                .build();
    }
}
