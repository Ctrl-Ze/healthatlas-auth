package com.healthatlas.auth.exception;

import java.time.Instant;

public record ErrorResponse(
        String error,
        String message,
        int status,
        Instant timestamp,
        String traceId
) {
    public static ErrorResponse of(Exception e, int status, String traceId) {
        return new ErrorResponse(
                e.getClass().getSimpleName(),
                e.getMessage(),
                status,
                Instant.now(),
                traceId
        );
    }
}
