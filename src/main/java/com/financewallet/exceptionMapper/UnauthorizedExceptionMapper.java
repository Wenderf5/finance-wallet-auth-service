package com.financewallet.exceptionMapper;

import com.financewallet.exceptions.UnauthorizedException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {
    private Gson gson = new Gson();

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(UnauthorizedException ex) {
        JsonObject responseJson = new JsonObject();

        responseJson.addProperty("status", Response.Status.UNAUTHORIZED.getStatusCode());
        responseJson.addProperty("message", ex.getMessage());

        String responseJsonString = this.gson.toJson(responseJson);

        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(responseJsonString)
                .build();
    }
}
