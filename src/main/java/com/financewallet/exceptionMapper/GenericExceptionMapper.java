package com.financewallet.exceptionMapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GenericExceptionMapper implements ExceptionMapper<RuntimeException> {
    private Gson gson = new Gson();

    public Response toResponse(RuntimeException ex) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", Response.Status.BAD_REQUEST.getStatusCode());
        responseJson.addProperty("message", ex.getMessage());
        String responseJsonString = this.gson.toJson(responseJson);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(responseJsonString)
                .build();
    }
}
