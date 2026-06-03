package com.financewallet.auth.domain.valueObjects;

import com.financewallet.auth.domain.exceptions.InvalidEmailException;

import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final String email;

    public Email(String email) {
        if (!isValid(email)) {
            throw new InvalidEmailException("Invalid email: " + email);
        }
        this.email = email;
    }

    private boolean isValid(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public String getEmail() {
        return email;
    }
}
