package com.financewallet.controllers;

import com.financewallet.DTOs.CreateUserDTO;
import com.financewallet.services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/v1/user")
public class UserController {
    @Inject
    private UserService userService;
    private Gson gson = new Gson();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Valid CreateUserDTO body) {
        String createUserSessionToken = this.userService.generateSaveAndSendEmailCode(body);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", Response.Status.OK.getStatusCode());
        responseJson.addProperty("message", "Confirmation code sent to email address '" + body.getEmail() + "'");
        String responseJsonString = this.gson.toJson(responseJson);

        NewCookie sessionCookie = new NewCookie.Builder("createUserSessionToken")
                .value(createUserSessionToken)
                .path("/")
                .maxAge(3600)
                .secure(false)
                .httpOnly(true)
                .build();

        return Response
                .status(Response.Status.OK)
                .cookie(sessionCookie)
                .entity(responseJsonString)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyCreateUserSessionToken(@CookieParam("createUserSessionToken") String createUserSessionToken) {
        if (createUserSessionToken == null || createUserSessionToken.isEmpty()) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        Long ttl = this.userService.verifyCreateUserSessionToken(createUserSessionToken);

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", Response.Status.OK.getStatusCode());
        responseJson.addProperty("seconds_to_expire:", ttl);

        String reponseJsonString = this.gson.toJson(responseJson);

        return Response
                .status(Response.Status.OK)
                .entity(reponseJsonString)
                .build();
    }
}
