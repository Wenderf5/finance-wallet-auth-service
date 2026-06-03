package com.financewallet.auth.infrastructure.exceptions;

public class CacheOperationException extends RuntimeException {
    public CacheOperationException(String message) {
        super(message);
    }

    public CacheOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
