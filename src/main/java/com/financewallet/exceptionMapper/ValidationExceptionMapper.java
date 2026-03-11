package com.financewallet.exceptionMapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private Gson gson = new Gson();

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(ConstraintViolationException ex) {
        JsonObject responseJson = new JsonObject();
        JsonObject errorElement = new JsonObject();

        ex.getConstraintViolations()
                .forEach(v -> errorElement.addProperty(
                        v.getPropertyPath().toString().substring(v.getPropertyPath().toString().lastIndexOf('.') + 1),
                        v.getMessage()));

        responseJson.addProperty("status", Response.Status.BAD_REQUEST.getStatusCode());
        responseJson.add("erros", errorElement);

        String responseJsonString = this.gson.toJson(responseJson);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(responseJsonString)
                .build();
    }
}
