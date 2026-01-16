package com.healthatlas.auth.login;

import com.healthatlas.auth.login.dto.LoginRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/auth/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    LoginService loginService;

    @POST
    public Response login(LoginRequest loginRequest) {
        var response = loginService.login(loginRequest);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }
}
