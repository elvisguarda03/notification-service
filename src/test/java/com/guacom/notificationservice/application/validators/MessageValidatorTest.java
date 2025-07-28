package com.guacom.notificationservice.application.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageValidatorTest {

    private MessageValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MessageValidator();
    }

    @Test
    void sanitizeContent_ShouldTrimAndNormalizeWhitespace() {
        // Arrange
        String messyContent = "  This   has\textra\nwhitespace  ";

        // Act
        String sanitized = validator.sanitizeContent(messyContent);

        // Assert
        assertEquals("This has extra whitespace", sanitized);
    }

    @Test
    void sanitizeContent_ShouldRemoveControlCharacters() {
        // Arrange
        String contentWithControlChars = "Valid content\u0000with\u0007control\u001Fchars";

        // Act
        String sanitized = validator.sanitizeContent(contentWithControlChars);

        // Assert
        assertEquals("Valid content with control chars", sanitized);
    }
}
