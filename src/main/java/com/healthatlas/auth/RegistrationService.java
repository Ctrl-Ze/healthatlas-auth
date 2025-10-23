package com.healthatlas.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jdbi.v3.core.Jdbi;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class RegistrationService {

    @Inject
    Jdbi jdbi;

    public UserResponseDto registerUser(RegistrationRequest request) {
        //TODO i need to insert to user_role table also
        String passwordHash = hashPassword(request.password());
        long id = jdbi.onDemand(RegistrationRepository.class)
                .insertUser(request.username(), request.email(), passwordHash, request.displayName());

        return new UserResponseDto(
                id,
                request.username(),
                request.email(),
                request.displayName()
        );
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
