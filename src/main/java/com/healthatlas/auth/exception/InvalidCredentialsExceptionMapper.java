package com.healthatlas.auth.exception;

import com.healthatlas.auth.config.TraceIdFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidCredentialsExceptionMapper implements ExceptionMapper<InvalidCredentialsException> {

    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(InvalidCredentialsException e) {
        String traceId = (String) requestContext.getProperty(TraceIdFilter.TRACE_ID);
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ErrorResponse.of(e, Response.Status.UNAUTHORIZED.getStatusCode(),traceId))
                .build();
    }
}