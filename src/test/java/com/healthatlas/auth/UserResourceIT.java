package com.healthatlas.auth;

import com.healthatlas.auth.login.dto.LoginRequest;
import com.healthatlas.auth.registration.dto.RegistrationRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// TODO: Add integration tests for JWT-protected endpoints using Authorization header.
// TODO: Add negative tests for expired or tampered tokens.

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
@DisplayName("User authentication and registration integration tests")
public class UserResourceIT {

    @Test
    void shouldCreateUser() {
        var request = new RegistrationRequest(
                "testUser1",
                "testUser1@example.com",
                "Supersecure22#",
                "Test User1"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/auth/register")
        .then()
                .statusCode(201)
                .body("username", equalTo("testUser1"))
                .body("email", equalTo("testUser1@example.com"));
    }

    @Test
    void shouldFailUsernameValidation() {
        var request = new RegistrationRequest(
                "",
                "testUser@example.com",
                "Supersecure22#",
                "Test User"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/api/v1/auth/register")
        .then()
                .statusCode(400)
                .body("error", equalTo("ResteasyReactiveViolationException"))
                .body("message", equalTo("register.request.username: must not be blank"))
                .body("timestamp", notNullValue())
                .body("traceId", notNullValue());
    }

    @Test
    void shouldFailEmailValidation() {
        var request = new RegistrationRequest(
                "testUser1",
                "",
                "Supersecure22#",
                "Test User"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("ResteasyReactiveViolationException"))
                .body("message", equalTo("register.request.email: must not be blank"))
                .body("timestamp", notNullValue())
                .body("traceId", notNullValue());
    }
    @Test
    void shouldFailPasswordValidation() {
        var request = new RegistrationRequest(
                "testUser2",
                "testUser2@example.com",
                "NotSecure",
                "Test User2"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(400)
                .body("error", equalTo("ResteasyReactiveViolationException"))
                .body("message", equalTo("register.request.password: Password must be at least 8 characters, contain uppercase, lowercase, a number, and a special character"))
                .body("timestamp", notNullValue())
                .body("traceId", notNullValue());
    }

    @Test
    void shouldFailsUserAlreadyExists() {
        var request = new RegistrationRequest(
                "testUser3",
                "testUser3@example.com",
                "Supersecure22#",
                "Test User3"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo("testUser3"))
                .body("email", equalTo("testUser3@example.com"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(409)
                .body("error", equalTo("UserAlreadyExistsException"))
                .body("message", equalTo("Username testUser3 already exists."))
                .body("timestamp", notNullValue())
                .body("traceId", notNullValue());
    }

    @Test
    void shouldLoginUser() {
        var request = new RegistrationRequest(
                "testUser4",
                "testUser4@example.com",
                "Supersecure22#",
                "Test User4"
        );

        var login = new LoginRequest(
                "testUser4",
                "Supersecure22#"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo("testUser4"))
                .body("email", equalTo("testUser4@example.com"));

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("username", equalTo("testUser4"))
                .body("email", equalTo("testUser4@example.com"))
                .body("token", notNullValue());
    }

    @Test
    void shouldFailBadCredentials() {
        var request = new RegistrationRequest(
                "testUser5",
                "testUser5@example.com",
                "Supersecure22#",
                "Test User5"
        );

        var login = new LoginRequest(
                "testUser5",
                "WrongPassword"
        );

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo("testUser5"))
                .body("email", equalTo("testUser5@example.com"));

        given()
                .contentType(ContentType.JSON)
                .body(login)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(401)
                .body("error", equalTo("InvalidCredentialsException"))
                .body("message", equalTo("Wrong password."))
                .body("timestamp", notNullValue())
                .body("traceId", notNullValue());

        var badLogin = new LoginRequest(
                "badUsername",
                "Supersecure22#"
        );

        given()
                .contentType(ContentType.JSON)
                .body(badLogin)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(401)
                .body("error", equalTo("InvalidCredentialsException"))
                .body("message", equalTo("Wrong username or email."))
                .body("timestamp", notNullValue())
                .body("traceId", notNullValue());
    }
}
