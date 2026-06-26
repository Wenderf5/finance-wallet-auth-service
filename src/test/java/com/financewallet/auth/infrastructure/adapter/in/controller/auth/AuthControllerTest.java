package com.financewallet.auth.infrastructure.adapter.in.controller.auth;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financewallet.auth.application.exception.EmailAlreadyInUseException;
import com.financewallet.auth.application.exception.EmailCodeException;
import com.financewallet.auth.application.exception.EmailCodeExpiredException;
import com.financewallet.auth.application.usercase.CompleteUserRegistrationUseCase;
import com.financewallet.auth.application.usercase.StartUserRegistrationUseCase;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.CompleteUserRegistrationRequest;
import com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.StartUserRegistrationRequest;
import com.financewallet.auth.infrastructure.exception.CacheOperationException;

import jakarta.servlet.http.Cookie;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private StartUserRegistrationUseCase startUserRegistrationUseCase;

    @MockitoBean
    private CompleteUserRegistrationUseCase completeUserRegistrationUseCase;

    @Nested
    class SignUp {
        @Test
        @DisplayName("Should return 204 and a cookie email confirmation token")
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
            .andExpect(status().isNoContent())
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
            .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
        }
    }

    @Nested
    class SignUpConfirm {
        @Test
        @DisplayName("Should return 201 and cookies when everything is ok")
        public void shouldReturn201AndCookiesWhenEverythingIsOk() throws Exception {
            CompleteUserRegistrationRequest completeUserRegistrationRequest = new CompleteUserRegistrationRequest("123456");
            String signupSessionToken = "session-token";
            String accessToken = "access-token";

            when(completeUserRegistrationUseCase.execute("123456", signupSessionToken)).thenReturn(accessToken);

            mockMvc.perform(
                post("/api/v1/auth/sign-up/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("signup_session", signupSessionToken))
                .content(objectMapper.writeValueAsString(completeUserRegistrationRequest))
            )
            .andExpect(status().isCreated())
            .andExpect(cookie().exists("signup_session"))
            .andExpect(cookie().maxAge("signup_session", 0))
            .andExpect(cookie().exists("access_cookie"))
            .andExpect(cookie().value("access_cookie", accessToken));
        }

        @Test
        @DisplayName("Should return 400 when the request payload is invalid")
        public void shouldReturn400WhenPayloadIsInvalid() throws Exception {
            CompleteUserRegistrationRequest request = new CompleteUserRegistrationRequest("");

            mockMvc.perform(
                post("/api/v1/auth/sign-up/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors").exists());
        }

        @Test
        @DisplayName("Should return 400 when EmailCodeException is thrown")
        public void shouldReturn400WhenEmailCodeIsInvalid() throws Exception {
            CompleteUserRegistrationRequest request = new CompleteUserRegistrationRequest("123456");
            String sessionToken = "session-token";

            when(completeUserRegistrationUseCase.execute("123456", sessionToken))
                .thenThrow(new EmailCodeException(400, "Invalid e-mail code"));

            mockMvc.perform(
                post("/api/v1/auth/sign-up/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("signup_session", sessionToken))
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Invalid e-mail code"));
        }

        @Test
        @DisplayName("Should return 400 when EmailCodeExpiredException is thrown")
        public void shouldReturn400WhenCacheIsMissingOrExpired() throws Exception {
            CompleteUserRegistrationRequest request = new CompleteUserRegistrationRequest("123456");
            String signupSessionToken = "session-token";

            when(completeUserRegistrationUseCase.execute("123456", signupSessionToken)).thenThrow(new EmailCodeExpiredException(400, "Verification code has expired"));

            mockMvc.perform(
                post("/api/v1/auth/sign-up/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("signup_session", signupSessionToken))
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Verification code has expired"));
        }

        @Test
        @DisplayName("Should return 400 and generic error message when RuntimeException is thrown")
        public void shouldReturn400WhenRuntimeExceptionIsThrown() throws Exception {
            CompleteUserRegistrationRequest request = new CompleteUserRegistrationRequest("123456");
            String sessionToken = "session-token";

            when(completeUserRegistrationUseCase.execute("123456", sessionToken)).thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(
                post("/api/v1/auth/sign-up/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("signup_session", sessionToken))
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please try again later."));
        }
    }
}
