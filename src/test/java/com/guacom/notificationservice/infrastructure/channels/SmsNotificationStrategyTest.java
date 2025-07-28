package com.guacom.notificationservice.infrastructure.channels;

import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationResult;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmsNotificationStrategyTest {

    private SmsNotificationStrategy smsStrategy;
    private User validUser;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        smsStrategy = new SmsNotificationStrategy();

        validUser = new User(
                "user-1",
                "John Doe",
                "john.doe@email.com",
                "+1234567890",
                List.of(MessageCategory.FINANCE),
                List.of(NotificationChannel.SMS)
        );

        testMessage = Message.builder()
                .category(MessageCategory.FINANCE)
                .content("Important financial update for testing")
                .build();
    }

    @Test
    void getChannelType_ShouldReturnSms() {
        // Act
        NotificationChannel channelType = smsStrategy.getChannelType();

        // Assert
        assertEquals(NotificationChannel.SMS, channelType);
    }

    @Test
    void sendNotification_ShouldReturnSuccess_WithValidUser() {
        // Act
        NotificationResult result = smsStrategy.sendNotification(validUser, testMessage);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getExternalMessageId());
        assertTrue(result.getExternalMessageId().startsWith("SMS-"));
        assertNull(result.getErrorMessage());
    }

    @Test
    void sendNotification_ShouldReturnFailure_WithInvalidPhoneNumber() {
        // Arrange
        User invalidUser = new User(
                "user-2",
                "Jane Doe",
                "jane.doe@email.com",
                "invalid-phone",
                Arrays.asList(MessageCategory.FINANCE),
                Arrays.asList(NotificationChannel.SMS)
        );

        // Act
        NotificationResult result = smsStrategy.sendNotification(invalidUser, testMessage);

        // Assert
        assertFalse(result.isSuccess());
        assertNull(result.getExternalMessageId());
        assertEquals("Invalid phone number format", result.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "+1234567890",
            "1234567890",
            "+1 (234) 567-8900",
            "+44 20 7946 0958",
            "+1-555-123-4567"
    })
    void validateRecipient_ShouldReturnTrue_ForValidPhoneNumbers(String phoneNumber) {
        // Arrange
        User user = new User(
                "user-test",
                "Test User",
                "test@email.com",
                phoneNumber,
                Arrays.asList(MessageCategory.FINANCE),
                Arrays.asList(NotificationChannel.SMS)
        );

        // Act & Assert
        assertTrue(smsStrategy.validateRecipient(user));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "abcd1234567890",
            "",
            "123-45678"
    })
    void validateRecipient_ShouldReturnFalse_ForInvalidPhoneNumbers(String phoneNumber) {
        // Arrange
        User user = new User(
                "user-test",
                "Test User",
                "test@email.com",
                phoneNumber,
                Arrays.asList(MessageCategory.FINANCE),
                Arrays.asList(NotificationChannel.SMS)
        );

        // Act & Assert
        assertFalse(smsStrategy.validateRecipient(user));
    }

    @Test
    void formatMessage_ShouldCreateProperSmsFormat() {
        // Act
        String formattedMessage = smsStrategy.formatMessage(testMessage, validUser);

        // Assert
        assertTrue(formattedMessage.contains("Hi John Doe!"));
        assertTrue(formattedMessage.contains("[Finance]"));
        assertTrue(formattedMessage.contains("Important financial update for testing"));
    }

    @Test
    void formatMessage_ShouldTruncateLongMessages() {
        // Arrange
        String longContent = "A".repeat(200);
        Message longMessage = Message.builder()
                .category(MessageCategory.FINANCE)
                .content(longContent)
                .build();

        // Act
        String formattedMessage = smsStrategy.formatMessage(longMessage, validUser);

        // Assert
        assertTrue(formattedMessage.length() <= 160);
        assertTrue(formattedMessage.endsWith("..."));
    }
}
