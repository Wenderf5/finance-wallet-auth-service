package com.financewallet.auth.infrastructure.adapters.in.controllers.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(max = 75, message = "The username must be up to 75 characters long")
    private String userName;

    @NotBlank(message = "The email address cannot be blank")
    @Email(message = "The email address must be valid")
    @Size(max = 255, message = "The email must be up to 255 characters long")
    private String email;

    @NotBlank(message = "The password cannot be blank")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).+$", message = "The password must include at least one uppercase letter, one lowercase letter, one number, and no spaces")
    @Size(min = 8, max = 128, message = "The password must be at least 8 characters long and no more than 128 characters long")
    private String password;
}
