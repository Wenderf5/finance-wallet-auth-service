package com.financewallet.auth.domain.valueObjects;

import com.financewallet.auth.domain.exceptions.InvalidPasswordException;

public class Password {
    private final String password;

    public Password(String password) {
        if (!isValid(password)) {
            throw new InvalidPasswordException(
                    "Invalid password: password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number.");
        }
        this.password = password;
    }

    private boolean isValid(String password) {
        if (password == null || password.length() < 8 || password.isBlank()) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            }
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (hasUpperCase && hasLowerCase && hasDigit) {
                return true;
            }
        }

        return false;
    }

    public String getPassword() {
        return password;
    }
}
