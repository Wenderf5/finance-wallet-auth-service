package com.financewallet.auth.infrastructure.adapters.in.controllers.advices.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenericExceptionResponse {
    private int status;
    private String message;
}
