package com.guacom.notificationservice.infrastructure.channels;

import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationResult;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.interfaces.NotificationChannelStrategy;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.logging.Logger;

@Component
public class PushNotificationStrategy implements NotificationChannelStrategy {
    private static final Logger logger = Logger.getLogger(PushNotificationStrategy.class.getName());
    private static final int PUSH_TITLE_MAX_LENGTH = 50;
    private static final int PUSH_BODY_MAX_LENGTH = 200;

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.PUSH;
    }

    @Override
    public NotificationResult sendNotification(User user, Message message) {
        try {
            logger.info(String.format("Sending Push Notification to user %s", user.getName()));

            if (!validateRecipient(user)) {
                return NotificationResult.failure("User not registered for push notifications");
            }

            String title = generateTitle(message);
            String body = formatMessage(message, user);
            String externalMessageId = simulatePushDelivery(user.getId(), title, body);

            logger.info(String.format("Push notification sent successfully. External ID: %s", externalMessageId));
            return NotificationResult.success(externalMessageId);

        } catch (Exception e) {
            logger.severe(String.format("Failed to send push notification to user %s: %s", user.getId(), e.getMessage()));
            return NotificationResult.failure("Push notification delivery failed: " + e.getMessage());
        }
    }

    @Override
    public boolean validateRecipient(User user) {
        // In a real implementation, we would check if the user has a valid device token
        // For simulation purposes, we assume all users can receive push notifications
        return user != null && user.getId() != null && !user.getId().trim().isEmpty();
    }

    @Override
    public String formatMessage(Message message, User user) {
        String content = message.getContent();

        // Truncate if too long for push notification
        if (content.length() > PUSH_BODY_MAX_LENGTH) {
            content = content.substring(0, PUSH_BODY_MAX_LENGTH - 3) + "...";
        }

        return content;
    }

    private String generateTitle(Message message) {
        String title = String.format("%s Update", message.getCategory().getDisplayName());

        if (title.length() > PUSH_TITLE_MAX_LENGTH) {
            title = title.substring(0, PUSH_TITLE_MAX_LENGTH - 3) + "...";
        }

        return title;
    }

    private String simulatePushDelivery(String userId, String title, String body) {
        // Simulate Push Notification API call
        String externalId = "PUSH-" + UUID.randomUUID().toString().substring(0, 8);

        // Log the simulated push notification
        logger.info("🔔 PUSH NOTIFICATION SIMULATION 🔔");
        logger.info(String.format("User ID: %s", userId));
        logger.info(String.format("Title: %s", title));
        logger.info(String.format("Body: %s", body));
        logger.info(String.format("External ID: %s", externalId));
        logger.info("Push notification delivered successfully (simulated)");

        return externalId;
    }
}
