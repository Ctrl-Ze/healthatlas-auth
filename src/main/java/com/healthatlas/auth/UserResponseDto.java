package com.healthatlas.auth;

public record UserResponseDto(
        long id,
        String username,
        String email,
        String displayName
) {}
