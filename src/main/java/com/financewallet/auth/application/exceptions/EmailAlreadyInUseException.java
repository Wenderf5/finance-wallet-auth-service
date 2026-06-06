package com.financewallet.auth.application.exceptions;

public class EmailAlreadyInUseException extends BusinessException {
    public EmailAlreadyInUseException(int status, String message) {
        super(status, message);
    }
}
