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

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationStrategyTest {

    private EmailNotificationStrategy emailStrategy;
    private User validUser;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        emailStrategy = new EmailNotificationStrategy();

        validUser = new User(
                "user-1",
                "John Doe",
                "john.doe@email.com",
                "+1234567890",
                Arrays.asList(MessageCategory.SPORTS),
                Arrays.asList(NotificationChannel.EMAIL)
        );

        testMessage = Message.builder()
                .category(MessageCategory.SPORTS)
                .content("Important sports update for testing")
                .build();
    }

    @Test
    void getChannelType_ShouldReturnEmail() {
        // Act
        NotificationChannel channelType = emailStrategy.getChannelType();

        // Assert
        assertEquals(NotificationChannel.EMAIL, channelType);
    }

    @Test
    void sendNotification_ShouldReturnSuccess_WithValidUser() {
        // Act
        NotificationResult result = emailStrategy.sendNotification(validUser, testMessage);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getExternalMessageId());
        assertTrue(result.getExternalMessageId().startsWith("EMAIL-"));
        assertNull(result.getErrorMessage());
    }

    @Test
    void sendNotification_ShouldReturnFailure_WithInvalidEmail() {
        // Arrange
        User invalidUser = new User(
                "user-2",
                "Jane Doe",
                "invalid-email",
                "+1234567890",
                Arrays.asList(MessageCategory.SPORTS),
                Arrays.asList(NotificationChannel.EMAIL)
        );

        // Act
        NotificationResult result = emailStrategy.sendNotification(invalidUser, testMessage);

        // Assert
        assertFalse(result.isSuccess());
        assertNull(result.getExternalMessageId());
        assertEquals("Invalid email address format", result.getErrorMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com",
            "user.name@domain.co.uk",
            "valid+email@subdomain.domain.org",
            "123@numeric-domain.com"
    })
    void validateRecipient_ShouldReturnTrue_ForValidEmails(String email) {
        // Arrange
        User user = new User(
                "user-test",
                "Test User",
                email,
                "+1234567890",
                Arrays.asList(MessageCategory.SPORTS),
                Arrays.asList(NotificationChannel.EMAIL)
        );

        // Act & Assert
        assertTrue(emailStrategy.validateRecipient(user));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "missing-at-sign.com",
            "no-domain@",
            "@no-local-part.com",
            "",
            "spaces in@email.com"
    })
    void validateRecipient_ShouldReturnFalse_ForInvalidEmails(String email) {
        // Arrange
        User user = new User(
                "user-test",
                "Test User",
                email,
                "+1234567890",
                Arrays.asList(MessageCategory.SPORTS),
                Arrays.asList(NotificationChannel.EMAIL)
        );

        // Act & Assert
        assertFalse(emailStrategy.validateRecipient(user));
    }

    @Test
    void formatMessage_ShouldCreateProperEmailFormat() {
        // Act
        String formattedMessage = emailStrategy.formatMessage(testMessage, validUser);

        // Assert
        assertTrue(formattedMessage.contains("Dear John Doe"));
        assertTrue(formattedMessage.contains("Sports"));
        assertTrue(formattedMessage.contains("Important sports update for testing"));
        assertTrue(formattedMessage.contains("Best regards"));
    }

    @Test
    public void sendNotification_ShouldHandleException_AndReturnFailureResult() {
        // Arrange
        EmailNotificationStrategy strategy = new EmailNotificationStrategy();
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        Message message = Message.builder().build();

        // Act
        NotificationResult result = strategy.sendNotification(user, message);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isSuccess(), "Result should indicate failure");
    }

}
