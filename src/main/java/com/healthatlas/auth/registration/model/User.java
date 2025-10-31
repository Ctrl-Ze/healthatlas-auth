package com.healthatlas.auth.registration.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
@Getter
@Setter
public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private String displayName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
}
