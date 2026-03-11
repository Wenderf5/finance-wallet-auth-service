package com.financewallet.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateUserDTO {
    @NotNull(message = "The field 'email' must not be null.")
    @NotEmpty(message = "The field 'email' must not be empty.")
    @NotBlank(message = "The field 'email' must not be blank.")
    @Email(message = "This email address is invalid.")
    @Pattern(regexp = "^\\S+$", message = "Emails cannot contain spaces.")
    private String email;

    @NotNull(message = "The field 'password' must not be null.")
    @NotEmpty(message = "The field 'password' must not be empty.")
    @NotBlank(message = "The field 'password' must not be blank.")
    @Size(min = 8, message = "The password must contain at least 8 characters.")
    @Pattern(regexp = "^\\S+$", message = "The password cannot contain spaces.")
    private String password;

    @NotNull(message = "The field 'userName' must not be null.")
    @NotEmpty(message = "The field 'userName' must not be empty.")
    @NotBlank(message = "The field 'userName' must not be blank.")
    private String userName;
}
