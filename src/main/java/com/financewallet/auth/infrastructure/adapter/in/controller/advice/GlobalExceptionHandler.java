package com.financewallet.auth.infrastructure.adapter.in.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.financewallet.auth.application.exception.BusinessException;
import com.financewallet.auth.infrastructure.adapter.in.controller.advice.dto.BusinessExceptionResponse;
import com.financewallet.auth.infrastructure.adapter.in.controller.advice.dto.GenericExceptionResponse;
import com.financewallet.auth.infrastructure.adapter.in.controller.advice.dto.ValidationExceptionResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ValidationExceptionResponse(HttpStatus.BAD_REQUEST.value(), errors));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BusinessExceptionResponse> handleBusinessException(BusinessException ex){
        return ResponseEntity
            .status(ex.getStatus())
            .body(new BusinessExceptionResponse(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GenericExceptionResponse> handleGenericException(RuntimeException ex){
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new GenericExceptionResponse(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred. Please try again later."));
    }
}
