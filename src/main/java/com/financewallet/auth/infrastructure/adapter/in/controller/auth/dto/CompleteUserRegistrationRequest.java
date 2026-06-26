package com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompleteUserRegistrationRequest {
    @NotBlank(message = "The emailCode field cannot be blank or null")
    @Size(min = 6, max = 6, message = "The emailCode field must have exactly 6 characters")
    private String emailCode;
}
