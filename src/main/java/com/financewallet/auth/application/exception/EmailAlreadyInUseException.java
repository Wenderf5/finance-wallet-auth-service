package com.financewallet.auth.application.exception;

public class EmailAlreadyInUseException extends BusinessException {
    public EmailAlreadyInUseException(int status, String message) {
        super(status, message);
    }
}
