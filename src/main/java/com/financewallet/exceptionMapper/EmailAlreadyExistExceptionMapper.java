package com.financewallet.exceptionMapper;

import com.financewallet.exceptions.EmailAlreadyExistException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class EmailAlreadyExistExceptionMapper implements ExceptionMapper<EmailAlreadyExistException> {
    private Gson gson = new Gson();

    @Override
    public Response toResponse(EmailAlreadyExistException ex) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("status", Response.Status.CONFLICT.getStatusCode());
        responseJson.addProperty("message", ex.getMessage());
        String responseJsonString = this.gson.toJson(responseJson);

        return Response
                .status(Response.Status.CONFLICT)
                .entity(responseJsonString)
                .build();
    }
}
