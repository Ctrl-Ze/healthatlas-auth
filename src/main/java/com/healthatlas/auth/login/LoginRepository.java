package com.healthatlas.auth.login;

import com.healthatlas.auth.registration.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.Optional;

public interface LoginRepository {

    @SqlQuery("""
            SELECT id, username, email, password_hash, display_name
            FROM users
            WHERE deleted_at IS NULL
                AND (username = :usernameOrEmail OR email = :usernameOrEmail)
            """)
    @RegisterBeanMapper(User.class)
    Optional<User> findUserByUsernameOrEmail(String usernameOrEmail);
}
