package com.healthatlas.auth.registration;

import com.healthatlas.auth.exception.EmailAlreadyExistsException;
import com.healthatlas.auth.exception.UserAlreadyExistsException;
import com.healthatlas.auth.registration.dto.RegistrationRequest;
import com.healthatlas.auth.registration.dto.UserResponse;
import com.healthatlas.auth.registration.model.RoleName;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.mindrot.jbcrypt.BCrypt;
import org.postgresql.util.PSQLException;

import java.util.EnumMap;
import java.util.Map;

@ApplicationScoped
public class RegistrationService {

    private final Map<RoleName, Integer> roleCache = new EnumMap<>(RoleName.class);

    @Inject
    Jdbi jdbi;

    @PostConstruct
    void init() {
        var roles = jdbi.onDemand(RegistrationRepository.class).getAllRoles();
        if (roles.isEmpty()) {
            throw new IllegalStateException("No roles found in database. Ensure seed data is loaded");
        }
        roles.forEach(role -> {
            try {
                RoleName name = RoleName.valueOf(role.getName().toUpperCase());
                roleCache.put(name, role.getId());
            } catch (IllegalArgumentException e) {
                        // Skip unknown DB roles
                    }
        });
    }

    @Transactional
    public UserResponse registerUser(RegistrationRequest request) {
        //TODO add maybe a new endpoint to upgrade user to admin/doctor
        String passwordHash = hashPassword(request.password());

        try {
            RegistrationRepository repo = jdbi.onDemand(RegistrationRepository.class);
            long userId = repo
                    .insertUser(request.username(), request.email(), passwordHash, request.displayName());
            int defaultRoleId = getRoleId(RoleName.USER);
            repo.insertUserRole(userId, defaultRoleId);

            return new UserResponse(
                    userId,
                    request.username(),
                    request.email(),
                    request.displayName()
            );
        } catch (UnableToExecuteStatementException e) {
            handleDatabaseException(e, request);
            throw e;
        }
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private void handleDatabaseException(UnableToExecuteStatementException e, RegistrationRequest request) {
        Throwable cause = e.getCause();
        if (cause instanceof PSQLException psqlEx) {
            if ("23505".equals(psqlEx.getSQLState())) {
                var serverMsg = psqlEx.getServerErrorMessage();
                var detail = serverMsg != null && serverMsg.getDetail() != null
                        ? serverMsg.getDetail().toLowerCase()
                        : "";
                if (detail.contains("username")) {
                    throw new UserAlreadyExistsException(request.username());
                } else if (detail.contains("email")) {
                    throw new EmailAlreadyExistsException(request.email());
                }
            }
        }
        throw e;
    }

    public int getRoleId(RoleName rolename) {
        Integer id = roleCache.get(rolename);
        if (id == null) {
            //TODO maybe add a new error
            throw new IllegalStateException("Role " + rolename + " not found in DB");
        }
        return id;
    }
}
