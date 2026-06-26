package com.financewallet.auth.application.exception;

public class EmailCodeException extends BusinessException {
    public EmailCodeException(int status, String message){
        super(status, message);
    }
}
