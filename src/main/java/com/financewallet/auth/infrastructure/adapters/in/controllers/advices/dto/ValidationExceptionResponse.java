package com.financewallet.auth.infrastructure.adapters.in.controllers.advices.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationExceptionResponse {
    private int status;
    private List<String> errors;
}
