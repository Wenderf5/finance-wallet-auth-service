package com.financewallet.auth.infrastructure.adapters.in.controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StartUserCreationRequest {
    @NotBlank(message = "The username cannot be blank")
    private String userName;

    @NotBlank(message = "The email address cannot be blank")
    @Email(message = "The email address must be valid")
    private String email;

    @NotBlank(message = "The password cannot be blank")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).{8,}$", message = "The password must be at least 8 characters long, contain no spaces, and include at least one uppercase letter, one lowercase letter, and one number")
    private String password;
}
