package com.financewallet.auth.application.exception;

public class EmailCodeExpiredException extends BusinessException  {
    public EmailCodeExpiredException(int status, String message){
        super(status, message);
    }
}
