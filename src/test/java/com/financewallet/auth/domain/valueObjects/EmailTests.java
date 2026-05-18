package com.financewallet.auth.domain.valueObjects;

import com.financewallet.auth.domain.exceptions.InvalidEmailException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTests {
    @Test
    void shouldCreateEmailWhenEmailIsValid() {
        assertDoesNotThrow(() -> new Email("user@email.com"));
    }

    @Test
    void shouldThrowInvalidEmailExceptionWhenEmailIsInvalid() {
        assertThrows(InvalidEmailException.class, () -> new Email("email-invalido"));
    }
}
