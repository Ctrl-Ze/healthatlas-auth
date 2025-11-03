package com.healthatlas.auth.login.dto;

// TODO: Consider returning token expiry timestamp and refresh token in future.
public record LoginResponse(
        String token,
        String username,
        String email
) {}
