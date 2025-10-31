package com.healthatlas.auth.registration.dto;

public record UserResponse(
        long id,
        String username,
        String email,
        String displayName
) {}
