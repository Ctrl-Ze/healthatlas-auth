package com.healthatlas.auth.registration.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegistrationRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).{8,}$",
                message = "Password must be at least 8 characters, contain uppercase, lowercase, a number, and a special character"
        ) String password,
        @NotBlank String displayName
) {}
