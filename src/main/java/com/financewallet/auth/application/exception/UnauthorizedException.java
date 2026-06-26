package com.financewallet.auth.application.exception;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(int status, String message) {
        super(status, message);
    }
}
