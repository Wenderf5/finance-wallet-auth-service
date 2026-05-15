package com.financewallet.auth.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CodeGeneratorServiceImpTests {
    private CodeGeneratorServiceImp codeGenerator = new CodeGeneratorServiceImp();

    @Test
    @DisplayName("Should generate a numeric code with 6 digits")
    public void shouldGenerateANumericCodeWith6digits() {
        String result = this.codeGenerator.generate();

        assertEquals(6, result.length());
        assertTrue(result.matches("\\d{6}"));
    }
}
