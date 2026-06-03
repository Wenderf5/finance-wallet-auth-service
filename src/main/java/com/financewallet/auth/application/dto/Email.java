package com.financewallet.auth.application.dto;

public class Email {
    private final String to;
    private final String subject;
    private final String content;

    public Email(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }

    public String getTo() {
        return this.to;
    }

    public String getSubject() {
        return this.subject;
    }

    public String getBody() {
        return this.content;
    }
}
