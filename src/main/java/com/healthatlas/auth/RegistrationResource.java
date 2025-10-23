package com.healthatlas.auth;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth/register")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistrationResource {

    @Inject
    RegistrationService registrationService;

    @POST
    public Response register(RegistrationRequest request) {
        UserResponseDto user =  registrationService.registerUser(request);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }
}
