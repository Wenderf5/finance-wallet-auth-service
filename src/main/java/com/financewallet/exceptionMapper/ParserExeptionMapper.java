package com.financewallet.exceptionMapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ParserExeptionMapper implements ExceptionMapper<ProcessingException> {
    private Gson gson = new Gson();

    public Response toResponse(ProcessingException ex) {
        JsonObject responseJson = new JsonObject();

        responseJson.addProperty("status", Response.Status.BAD_REQUEST.getStatusCode());
        responseJson.addProperty("message", "Invalid request body");

        String responseJsoString = gson.toJson(responseJson);

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(responseJsoString)
                .build();
    }
}
