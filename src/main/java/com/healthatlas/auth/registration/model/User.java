package com.healthatlas.auth.registration.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private long id;
    private UUID publicId;
    private String username;
    private String email;
    private String passwordHash;
    private String displayName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;

    public User(long id, UUID publicId, String username, String email, String passwordHash, String displayName) {
        this.id = id;
        this.publicId = publicId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }
}
