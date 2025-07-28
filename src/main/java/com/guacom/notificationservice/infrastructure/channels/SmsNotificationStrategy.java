package com.guacom.notificationservice.infrastructure.channels;

import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationResult;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.interfaces.NotificationChannelStrategy;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Component
public class SmsNotificationStrategy implements NotificationChannelStrategy {
    private static final Logger logger = Logger.getLogger(SmsNotificationStrategy.class.getName());
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{10,}$");
    private static final int SMS_MAX_LENGTH = 160;

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.SMS;
    }

    @Override
    public NotificationResult sendNotification(User user, Message message) {
        try {
            logger.info(String.format("Sending SMS to user %s (%s)", user.getName(), user.getPhoneNumber()));

            if (!validateRecipient(user)) {
                return NotificationResult.failure("Invalid phone number format");
            }

            String formattedMessage = formatMessage(message, user);
            String externalMessageId = simulateSmsDelivery(user.getPhoneNumber(), formattedMessage);

            logger.info(String.format("SMS sent successfully. External ID: %s", externalMessageId));
            return NotificationResult.success(externalMessageId);

        } catch (Exception e) {
            logger.severe(String.format("Failed to send SMS to %s: %s", user.getPhoneNumber(), e.getMessage()));
            return NotificationResult.failure("SMS delivery failed: " + e.getMessage());
        }
    }

    @Override
    public boolean validateRecipient(User user) {
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(user.getPhoneNumber().trim()).matches();
    }

    @Override
    public String formatMessage(Message message, User user) {
        String content = String.format("Hi %s! [%s] %s",
                user.getName(),
                message.getCategory().getDisplayName(),
                message.getContent());

        //Truncate if too long for SMS
        if (content.length() > SMS_MAX_LENGTH) {
            content = content.substring(0, SMS_MAX_LENGTH - 3) + "...";
        }

        return content;
    }

    private String simulateSmsDelivery(String phoneNumber, String message) {
        String externalId = "SMS-" + UUID.randomUUID().toString().substring(0, 8);

        logger.info(String.format("📱 SMS SIMULATION 📱"));
        logger.info(String.format("To: %s", phoneNumber));
        logger.info(String.format("Message: %s", message));
        logger.info(String.format("External ID: %s", externalId));
        logger.info("SMS delivered successfully (simulated)");

        return externalId;
    }
}
