package com.healthatlas.auth.exception;

import com.healthatlas.auth.config.TraceIdFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    @Context
    ContainerRequestContext requestContext;

    @Override
    public Response toResponse(DomainException e) {
        int status = mapStatus(e);
        String traceId = (String) requestContext.getProperty(TraceIdFilter.TRACE_ID);
        return Response.status(status)
                .entity(ErrorResponse.of(e, status, traceId))
                .build();
    }

    private int mapStatus(DomainException e) {
        if (e instanceof UserAlreadyExistsException || e instanceof EmailAlreadyExistsException) {
            return Response.Status.CONFLICT.getStatusCode();
        }
        return Response.Status.BAD_REQUEST.getStatusCode();
    }
}
