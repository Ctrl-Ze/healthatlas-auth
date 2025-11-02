package com.healthatlas.auth.login.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username or email must not be blank")
        String usernameOrEmail,
        @NotBlank(message = "password must not be blank")
        String password
) {
}