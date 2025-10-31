package com.healthatlas.auth.registration;

import com.healthatlas.auth.registration.model.Role;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

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

    @SqlUpdate("INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)")
    void insertUserRole(@Bind("userId") long userId, @Bind("roleId") int roleId);

    @SqlQuery("SELECT id,name FROM roles")
    @RegisterConstructorMapper(Role.class)
    List<Role> getAllRoles();

    @SqlQuery("SELECT id, name FROM roles WHERE name = :name ")
    @RegisterConstructorMapper(Role.class)
    Optional<Role> getRoleByName(@Bind("name") String roleName);
}
