package com.financewallet.auth.application.exceptions;

public class BusinessException extends RuntimeException {
    private int status;

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
