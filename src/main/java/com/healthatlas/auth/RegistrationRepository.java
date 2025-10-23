package com.healthatlas.auth;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface RegistrationRepository {

    @SqlUpdate("""
            INSERT INTO users (username, email, password_hash, display_name)
            VALUES (:username, :email, :passwordHash, :displayName)
            """)
    @GetGeneratedKeys("id")
    long insertUser(@Bind("username") String username,
                    @Bind("email") String email,
                    @Bind("passwordHash") String passwordHash,
                    @Bind("displayName") String displayName);
}
