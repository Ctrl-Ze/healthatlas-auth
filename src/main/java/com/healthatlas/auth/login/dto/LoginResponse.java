package com.healthatlas.auth.login.dto;

public record LoginResponse(
        String username,
        String email,
        String message
) {
}
