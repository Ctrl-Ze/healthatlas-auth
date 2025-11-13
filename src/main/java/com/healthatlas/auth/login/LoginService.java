package com.healthatlas.auth.login;

import com.healthatlas.auth.exception.InvalidCredentialsException;
import com.healthatlas.auth.login.dto.LoginRequest;
import com.healthatlas.auth.login.dto.LoginResponse;
import com.healthatlas.auth.registration.model.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jdbi.v3.core.Jdbi;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class LoginService {

    @Inject
    Jdbi jdbi;

    public LoginResponse login(LoginRequest loginRequest) {
        var repo = jdbi.onDemand(LoginRepository.class);
        User user = repo.findUserByUsernameOrEmail(loginRequest.usernameOrEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Wrong username or email."));

        if (!BCrypt.checkpw(loginRequest.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Wrong password.");
        }

        var roles = repo.findRolesByUsername(user.getUsername());
        String token = generateToken(user, roles);

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail()
        );
    }
    // TODO: [SECURITY] Move signing key to secure secret store or Vault before production.
    // TODO: [SECURITY] Rotate keys regularly and expose JWKS endpoint for other services.
    // TODO: [AUTHZ] Populate roles dynamically from the user record or permissions table.
    // TODO: [SESSION] Implement refresh tokens for long-lived sessions (mobile/web).
    // TODO: [AUDIT] Add jti (JWT ID) claim for audit tracing and revocation support.
    private String generateToken(User user, List<String> roles) {
        return Jwt.claims()
                .issuer("healthatlas")
                .subject(user.getUsername())
                .upn(user.getEmail())
                .groups(Set.copyOf(roles))
                .claim("user_id", user.getPublicId())
                .claim("email", user.getEmail())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600)) //1h validity
                .sign();
    }
}
