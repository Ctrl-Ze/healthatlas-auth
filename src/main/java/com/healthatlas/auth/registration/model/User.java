package com.healthatlas.auth.registration.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
@Getter
@Setter
@NoArgsConstructor
public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private String displayName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;

    public User(long id, String username, String email, String passwordHash, String displayName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }
}
