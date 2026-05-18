package com.financewallet.auth.domain.valueObjects;

import com.financewallet.auth.domain.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTests {
    @Test
    void shouldCreatePasswordWhenPasswordIsValid() {
        assertDoesNotThrow(() -> new Password("ValidPass1"));
    }

    @Test
    void shouldThrowInvalidPasswordExceptionWhenPasswordIsInvalid() {
        assertThrows(InvalidPasswordException.class, () -> new Password("invalid"));
    }
}
