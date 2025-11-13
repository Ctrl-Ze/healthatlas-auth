package com.healthatlas.auth.login;

import com.healthatlas.auth.exception.InvalidCredentialsException;
import com.healthatlas.auth.login.dto.LoginRequest;
import com.healthatlas.auth.login.dto.LoginResponse;
import com.healthatlas.auth.registration.model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class LoginServiceTest {
    @Mock
    Jdbi jdbi;

    @Mock
    LoginRepository repo;

    @InjectMocks
    LoginService loginService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoginSuccessfully() {
        String hashedPassword = BCrypt.hashpw("Supersecure22#", BCrypt.gensalt());
        User user = new User(1L, UUID.randomUUID(), "alex", "alex@example.com", hashedPassword, "Alex Cretu");

        when(jdbi.onDemand(LoginRepository.class)).thenReturn(repo);
        when(repo.findUserByUsernameOrEmail("alex")).thenReturn(Optional.of(user));
        when(repo.findRolesByUsername("alex")).thenReturn(List.of("USER"));

        var request = new LoginRequest("alex", "Supersecure22#");
        LoginResponse response = loginService.login(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("alex", response.username());
        Assertions.assertEquals("alex@example.com", response.email());
        Assertions.assertNotNull(response.token());
        Assertions.assertTrue(response.token().startsWith("ey")); // looks like a JWT
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(jdbi.onDemand(LoginRepository.class)).thenReturn(repo);
        when(repo.findUserByUsernameOrEmail("unknown")).thenReturn(Optional.empty());

        var request = new LoginRequest("unknown", "password");

        InvalidCredentialsException e =  Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> loginService.login(request)
        );
        Assertions.assertEquals("Wrong username or email.", e.getMessage());
    }

    @Test
    void shouldFailWhenPasswordInvalid() {
        String hashedPassword = BCrypt.hashpw("Supersecure22#", BCrypt.gensalt());
        User user = new User(1L, UUID.randomUUID(), "alex", "alex@example.com", hashedPassword, "Alex Cretu");

        when(jdbi.onDemand(LoginRepository.class)).thenReturn(repo);
        when(repo.findUserByUsernameOrEmail("alex")).thenReturn(Optional.of(user));

        var request = new LoginRequest("alex", "WrongPassword");

        InvalidCredentialsException e = Assertions.assertThrows(
                InvalidCredentialsException.class,
                () -> loginService.login(request)
        );
        Assertions.assertEquals("Wrong password.", e.getMessage());
    }
}
