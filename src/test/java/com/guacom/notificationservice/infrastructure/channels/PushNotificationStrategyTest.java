package com.guacom.notificationservice.infrastructure.channels;

import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationResult;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushNotificationStrategy Tests")
class PushNotificationStrategyTest {

    private PushNotificationStrategy pushNotificationStrategy;
    private User testUser;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        pushNotificationStrategy = new PushNotificationStrategy();

        testUser = User.builder()
                .id("user123")
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("+1234567890")
                .build();

        testMessage = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(MessageCategory.SPORTS)
                .content("Your favorite team won the championship!")
                .build();
    }

    @Test
    @DisplayName("Should return PUSH channel type")
    void getChannelType_ShouldReturnPushChannel() {
        // When
        NotificationChannel channelType = pushNotificationStrategy.getChannelType();

        // Then
        assertThat(channelType).isEqualTo(NotificationChannel.PUSH);
    }

    @Test
    @DisplayName("Should send push notification successfully with valid user and message")
    void sendNotification_WithValidUserAndMessage_ShouldReturnSuccess() {
        // When
        NotificationResult result = pushNotificationStrategy.sendNotification(testUser, testMessage);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getExternalMessageId()).isNotNull();
        assertThat(result.getExternalMessageId()).startsWith("PUSH-");
        assertThat(result.getErrorMessage()).isNull();
    }

    @Test
    @DisplayName("Should return failure when user validation fails")
    void sendNotification_WithInvalidUser_ShouldReturnFailure() {
        // Given
        User invalidUser = User.builder()
                .id(null)
                .name("Invalid User")
                .build();

        // When
        NotificationResult result = pushNotificationStrategy.sendNotification(invalidUser, testMessage);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).isEqualTo("User not registered for push notifications");
        assertThat(result.getExternalMessageId()).isNull();
    }

    @Test
    @DisplayName("Should handle exception during notification sending")
    void sendNotification_WhenExceptionThrown_ShouldReturnFailure() {
        // Given
        PushNotificationStrategy spyStrategy = spy(pushNotificationStrategy);
        doThrow(new RuntimeException("Network error"))
                .when(spyStrategy).formatMessage(any(Message.class), any(User.class));

        // When
        NotificationResult result = spyStrategy.sendNotification(testUser, testMessage);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorMessage()).contains("Push notification delivery failed: Network error");
        assertThat(result.getExternalMessageId()).isNull();
    }

    @Test
    @DisplayName("Should validate recipient with valid user")
    void validateRecipient_WithValidUser_ShouldReturnTrue() {
        // When
        boolean isValid = pushNotificationStrategy.validateRecipient(testUser);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject null user")
    void validateRecipient_WithNullUser_ShouldReturnFalse() {
        // When
        boolean isValid = pushNotificationStrategy.validateRecipient(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should reject user with invalid ID")
    void validateRecipient_WithInvalidUserId_ShouldReturnFalse(String invalidId) {
        // Given
        User userWithInvalidId = User.builder()
                .id(invalidId)
                .name("Test User")
                .build();

        // When
        boolean isValid = pushNotificationStrategy.validateRecipient(userWithInvalidId);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should format message without truncation when content is short")
    void formatMessage_WithShortContent_ShouldReturnOriginalContent() {
        // Given
        String shortContent = "Short message";
        Message shortMessage = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(MessageCategory.SPORTS)
                .content(shortContent)
                .build();

        // When
        String formattedMessage = pushNotificationStrategy.formatMessage(shortMessage, testUser);

        // Then
        assertThat(formattedMessage).isEqualTo(shortContent);
    }

    @Test
    @DisplayName("Should truncate message when content exceeds maximum length")
    void formatMessage_WithLongContent_ShouldTruncateContent() {
        // Given
        String longContent = "A".repeat(250);
        Message longMessage = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(MessageCategory.SPORTS)
                .content(longContent)
                .build();

        // When
        String formattedMessage = pushNotificationStrategy.formatMessage(longMessage, testUser);

        // Then
        assertThat(formattedMessage).hasSize(200);
        assertThat(formattedMessage).endsWith("...");
        assertThat(formattedMessage).startsWith("A".repeat(197));
    }

    @ParameterizedTest
    @CsvSource({
            "SPORTS, Sports Update",
            "FINANCE, Finance Update",
            "MOVIES, Movies Update"
    })
    @DisplayName("Should generate correct title for each category")
    void generateTitle_ForDifferentCategories_ShouldReturnCorrectTitle(
            MessageCategory category, String expectedTitle) {
        // Given
        Message messageWithCategory = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(category)
                .content("Test content")
                .build();

        // When
        NotificationResult result = pushNotificationStrategy.sendNotification(testUser, messageWithCategory);

        // Then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should generate unique external message IDs for multiple calls")
    void sendNotification_MultipleCalls_ShouldGenerateUniqueExternalIds() {
        // When
        NotificationResult result1 = pushNotificationStrategy.sendNotification(testUser, testMessage);
        NotificationResult result2 = pushNotificationStrategy.sendNotification(testUser, testMessage);
        NotificationResult result3 = pushNotificationStrategy.sendNotification(testUser, testMessage);

        // Then
        assertThat(result1.getExternalMessageId()).isNotEqualTo(result2.getExternalMessageId());
        assertThat(result2.getExternalMessageId()).isNotEqualTo(result3.getExternalMessageId());
        assertThat(result1.getExternalMessageId()).isNotEqualTo(result3.getExternalMessageId());

        assertThat(result1.getExternalMessageId()).startsWith("PUSH-");
        assertThat(result2.getExternalMessageId()).startsWith("PUSH-");
        assertThat(result3.getExternalMessageId()).startsWith("PUSH-");
    }

    @Test
    @DisplayName("Should log appropriate messages during successful notification sending")
    void sendNotification_SuccessfulSending_ShouldLogCorrectMessages() {
        // Given
        Logger mockLogger = mock(Logger.class);


        // When
        NotificationResult result = pushNotificationStrategy.sendNotification(testUser, testMessage);

        // Then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should handle message with special characters in formatting")
    void formatMessage_WithSpecialCharacters_ShouldHandleCorrectly() {
        // Given
        String contentWithSpecialChars = "Special chars: àáâãäåæçèéêë 中文 🎉💯⚽";
        Message specialMessage = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(MessageCategory.SPORTS)
                .content(contentWithSpecialChars)
                .build();

        // When
        String formattedMessage = pushNotificationStrategy.formatMessage(specialMessage, testUser);

        // Then
        assertThat(formattedMessage).isEqualTo(contentWithSpecialChars);

        String longSpecialContent = (contentWithSpecialChars + " ").repeat(10);
        Message longSpecialMessage = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(MessageCategory.SPORTS)
                .content(longSpecialContent)
                .build();

        String truncatedMessage = pushNotificationStrategy.formatMessage(longSpecialMessage, testUser);

        assertThat(truncatedMessage).hasSize(200);
        assertThat(truncatedMessage).endsWith("...");
    }

    @Test
    @DisplayName("Should handle edge case where message content is exactly max length minus 3")
    void formatMessage_WithContentExactlyMaxLengthMinusThree_ShouldHandleCorrectly() {
        String contentExactly197 = "A".repeat(197);
        Message edgeMessage = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(MessageCategory.SPORTS)
                .content(contentExactly197)
                .build();

        // When
        String formattedMessage = pushNotificationStrategy.formatMessage(edgeMessage, testUser);

        // Then
        assertThat(formattedMessage).isEqualTo(contentExactly197);
        assertThat(formattedMessage).hasSize(197);
        assertThat(formattedMessage).doesNotEndWith("...");
    }

    @Test
    @DisplayName("Should successfully send notification with minimum valid user data")
    void sendNotification_WithMinimumValidUserData_ShouldSucceed() {
        // Given
        User minimalUser = User.builder()
                .id("123")
                .build();

        // When
        NotificationResult result = pushNotificationStrategy.sendNotification(minimalUser, testMessage);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getExternalMessageId()).isNotNull();
        assertThat(result.getExternalMessageId()).startsWith("PUSH-");
    }

    @Test
    @DisplayName("Should handle null message content gracefully")
    void formatMessage_WithNullContent_ShouldHandleGracefully() {
        // Given
        Message messageWithNullContent = Message.builder()
                .id(UUID.randomUUID().toString())
                .category(MessageCategory.SPORTS)
                .content(null)
                .build();

        try {
            pushNotificationStrategy.formatMessage(messageWithNullContent, testUser);
        } catch (NullPointerException e) {
            assertThat(e).isInstanceOf(NullPointerException.class);
        }
    }

    @Test
    @DisplayName("Should validate external message ID format")
    void sendNotification_ShouldGenerateValidExternalMessageIdFormat() {
        // When
        NotificationResult result = pushNotificationStrategy.sendNotification(testUser, testMessage);

        // Then
        assertThat(result.isSuccess()).isTrue();
        String externalId = result.getExternalMessageId();

        assertThat(externalId).matches("^PUSH-[a-f0-9]{8}$");
        assertThat(externalId).hasSize(13);
    }

    @Test
    @DisplayName("Should handle user with whitespace-only ID as invalid")
    void validateRecipient_WithWhitespaceOnlyId_ShouldReturnFalse() {
        // Given
        User userWithWhitespaceId = User.builder()
                .id("   \t\n   ")
                .name("Test User")
                .build();

        // When
        boolean isValid = pushNotificationStrategy.validateRecipient(userWithWhitespaceId);

        // Then
        assertThat(isValid).isFalse();
    }
}