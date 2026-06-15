package com.financewallet.auth.infrastructure.adapter.in.controller.auth;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financewallet.auth.application.exception.EmailAlreadyInUseException;
import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.StartUserRegistrationRequest;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private StartUserRegistrationUseCase startUserRegistrationUseCase;

    @Test
    @DisplayName("Should return 200 and a cookie email confirmation token")
    public void shouldReturn200AndCookieEmailConfirmationToken() throws Exception {
        String userName = "test_name";
        String email = "test@example.com";
        String password = "Ab123456";
        String token = "test-token";

        when(startUserRegistrationUseCase.execute(userName, email, password)).thenReturn(token);

        mockMvc.perform(
            post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new StartUserRegistrationRequest(userName, email, password)))
        )
        .andExpect(status().isOk())
        .andExpect(header().exists(HttpHeaders.SET_COOKIE))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("signup_session")));
    }

    @Test
    @DisplayName("Should return 400 when the request payload is invalid")
    public void shouldReturn400WhenPayloadIsInvalid() throws Exception {
        String userName = "";
        String email = "invalid-email";
        String password = "short";

        mockMvc.perform(
            post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new StartUserRegistrationRequest(userName, email, password)))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("Should return 409 when EmailAlreadyInUseException is thrown")
    public void shouldReturn400WhenEmailAlreadyInUseExceptionIsThrown() throws Exception {
        String userName = "test_name";
        String email = "test@example.com";
        String password = "Ab123456";

        when(startUserRegistrationUseCase.execute(userName, email, password))
            .thenThrow(new EmailAlreadyInUseException(409, "A user with this email address already exists"));

        mockMvc.perform(
            post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new StartUserRegistrationRequest(userName, email, password)))
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("A user with this email address already exists"));
    }

    @Test
    @DisplayName("Should return 400 and generic error message when RuntimeException is thrown")
    public void shouldReturn400WhenRuntimeExceptionIsThrown() throws Exception {
        String userName = "test_name";
        String email = "test@example.com";
        String password = "Ab123456";

        when(startUserRegistrationUseCase.execute(userName, email, password))
            .thenThrow(new RuntimeException("Error registering user"));

        mockMvc.perform(
            post("/api/v1/auth/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new StartUserRegistrationRequest(userName, email, password)))
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Error registering user"));
    }
}
