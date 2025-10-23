package com.healthatlas.auth;

public record RegistrationRequest(
        String username,
        String email,
        String password,
        String displayName
) {}
