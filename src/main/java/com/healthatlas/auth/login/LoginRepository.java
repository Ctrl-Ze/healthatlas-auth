package com.healthatlas.auth.login;

import com.healthatlas.auth.registration.model.User;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.Optional;

public interface LoginRepository {

    @SqlQuery("""
            SELECT id, public_id, username, email, password_hash, display_name
            FROM users
            WHERE deleted_at IS NULL
                AND (username = :usernameOrEmail OR email = :usernameOrEmail)
            """)
    @RegisterBeanMapper(User.class)
    Optional<User> findUserByUsernameOrEmail(String usernameOrEmail);

    @SqlQuery("""
            SELECT r.name
            FROM roles r
            JOIN user_roles ur ON ur.role_id = r.id
            JOIN users u ON ur.user_id = u.id
            WHERE u.username = :username
            """)
    List<String> findRolesByUsername(String username);
}
