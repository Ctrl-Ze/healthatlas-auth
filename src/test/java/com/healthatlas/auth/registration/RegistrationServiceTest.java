package com.healthatlas.auth.registration;

import com.healthatlas.auth.exception.EmailAlreadyExistsException;
import com.healthatlas.auth.exception.UserAlreadyExistsException;
import com.healthatlas.auth.registration.dto.RegistrationRequest;
import com.healthatlas.auth.registration.dto.UserResponse;
import com.healthatlas.auth.registration.model.Role;
import com.healthatlas.auth.registration.model.RoleName;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    @Mock
    Jdbi jdbi;

    @Mock
    RegistrationRepository repo;

    @InjectMocks
    RegistrationService registrationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // given
        var request = new RegistrationRequest(
                "alex",
                "alex@example.com",
                "Supersecure22#",
                "Alex Cretu"
        );

        when(jdbi.onDemand(RegistrationRepository.class)).thenReturn(repo);
        when(repo.insertUser(anyString(), anyString(), anyString(), anyString())).thenReturn(1L);
        when(repo.getAllRoles()).thenReturn(List.of(new Role(1, RoleName.USER.name())));
        registrationService.init();

        // when
        UserResponse response = registrationService.registerUser(request);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals("alex", response.username());
        verify(repo).insertUserRole(1L, registrationService.getRoleId(RoleName.USER));
        verify(repo).insertUserRole(anyLong(), eq(1));
    }


    @Test
    void shouldThrowUserAlreadyExistsException() {
        // given
        var request = new RegistrationRequest(
                "alex",
                "alex@example.com",
                "Supersecure22#",
                "Alex Cretu"
        );

        var serverError = mock(ServerErrorMessage.class);
        when(serverError.getDetail()).thenReturn("Key (username)=(alex) already exists.");

        var psqlEx = mock(PSQLException.class);
        when(psqlEx.getSQLState()).thenReturn("23505");
        when(psqlEx.getServerErrorMessage()).thenReturn(serverError);

        var unable = new UnableToExecuteStatementException(psqlEx, null);

        when(jdbi.onDemand(RegistrationRepository.class)).thenReturn(repo);
        when(repo.insertUser(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(unable);

        //when + then
        assertThrows(UserAlreadyExistsException.class, () -> registrationService.registerUser(request));
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {
        // given
        var request = new RegistrationRequest(
                "alex",
                "alex@example.com",
                "Supersecure22#",
                "Alex Cretu"
        );

        var serverError = mock(ServerErrorMessage.class);
        when(serverError.getDetail()).thenReturn("Key (email)=(alex@example.com) already exists.");

        var psqlEx = mock(PSQLException.class);
        when(psqlEx.getSQLState()).thenReturn("23505");
        when(psqlEx.getServerErrorMessage()).thenReturn(serverError);

        var unable = new UnableToExecuteStatementException(psqlEx, null);

        when(jdbi.onDemand(RegistrationRepository.class)).thenReturn(repo);
        when(repo.insertUser(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(unable);

        //when + then
        assertThrows(EmailAlreadyExistsException.class, () -> registrationService.registerUser(request));
    }

    @Test
    void shouldThrowWhenRoleMissing() {
        RegistrationService newService = new RegistrationService();
        newService.jdbi = jdbi;

        when(jdbi.onDemand(RegistrationRepository.class)).thenReturn(repo);
        when(repo.getAllRoles()).thenReturn(List.of());

        assertThrows(IllegalStateException.class, newService::init);
    }
}
