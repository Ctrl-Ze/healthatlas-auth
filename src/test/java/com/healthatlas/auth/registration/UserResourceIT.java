package com.healthatlas.auth.registration;

import com.healthatlas.auth.PostgresTestResource;
import com.healthatlas.auth.registration.dto.RegistrationRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
public class UserResourceIT {

    @Test
    void shouldCreateUser() {
        var request = new RegistrationRequest(
                "alex",
                "alex@example.com",
                "Supersecure22#",
                "Alex Cretu"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/auth/register")
        .then()
                .statusCode(201)
                .body("username", equalTo("alex"))
                .body("email", equalTo("alex@example.com"));
    }

    @Test
    void shouldFailUsernameValidation() {
        var request = new RegistrationRequest(
                "",
                "alex@example.com",
                "Supersecure22#",
                "Alex Cretu"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
        .when()
                .post("/auth/register")
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
                "alex",
                "",
                "Supersecure22#",
                "Alex Cretu"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/auth/register")
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
                "alex",
                "alex@example.com",
                "NotSecure",
                "Alex Cretu"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/auth/register")
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
                "george",
                "george@example.com",
                "Supersecure22#",
                "George Joseph"
        );
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo("george"))
                .body("email", equalTo("george@example.com"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(409)
                .body("error", equalTo("UserAlreadyExistsException"))
                .body("message", equalTo("Username george already exists."))
                .body("timestamp", notNullValue())
                .body("traceId", notNullValue());
    }
}
