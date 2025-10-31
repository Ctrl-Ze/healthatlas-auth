package com.healthatlas.auth.registration;

import com.healthatlas.auth.registration.dto.RegistrationRequest;
import com.healthatlas.auth.registration.dto.UserResponse;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    public Response register(@Valid RegistrationRequest request) {
        UserResponse user =  registrationService.registerUser(request);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }
}
