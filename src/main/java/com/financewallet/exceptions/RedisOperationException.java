package com.financewallet.exceptions;

public class RedisOperationException extends RuntimeException {
    public RedisOperationException(String msg) {
        super(msg);
    }
}
