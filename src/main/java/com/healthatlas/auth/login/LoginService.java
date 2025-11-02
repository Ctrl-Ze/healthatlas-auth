package com.healthatlas.auth.login;

import com.healthatlas.auth.exception.InvalidCredentialsException;
import com.healthatlas.auth.login.dto.LoginRequest;
import com.healthatlas.auth.login.dto.LoginResponse;
import com.healthatlas.auth.registration.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jdbi.v3.core.Jdbi;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class LoginService {

    @Inject
    Jdbi jdbi;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = jdbi.onDemand(LoginRepository.class)
                .findUserByUsernameOrEmail(loginRequest.usernameOrEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Wrong username or email."));

        if (!BCrypt.checkpw(loginRequest.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Wrong password.");
        }

        return new LoginResponse(
                user.getUsername(),
                user.getEmail(),
                "Login successful");
    }
}
