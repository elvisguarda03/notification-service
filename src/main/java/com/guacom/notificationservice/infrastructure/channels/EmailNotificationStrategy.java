package com.guacom.notificationservice.infrastructure.channels;

import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationResult;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.interfaces.NotificationChannelStrategy;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Component
public class EmailNotificationStrategy implements NotificationChannelStrategy {
    private static final Logger logger = Logger.getLogger(EmailNotificationStrategy.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public NotificationResult sendNotification(User user, Message message) {
        try {
            logger.info(String.format("Sending Email to user %s (%s)", user.getName(), user.getEmail()));

            if (!validateRecipient(user)) {
                return NotificationResult.failure("Invalid email address format");
            }

            String subject = generateSubject(message);
            String body = formatMessage(message, user);
            String externalMessageId = simulateEmailDelivery(user.getEmail(), subject, body);

            logger.info(String.format("Email sent successfully. External ID: %s", externalMessageId));
            return NotificationResult.success(externalMessageId);

        } catch (Exception e) {
            logger.severe(String.format("Failed to send email to %s: %s", user.getEmail(), e.getMessage()));
            return NotificationResult.failure("Email delivery failed: " + e.getMessage());
        }
    }

    @Override
    public boolean validateRecipient(User user) {
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(user.getEmail().trim()).matches();
    }

    @Override
    public String formatMessage(Message message, User user) {
        return String.format("""
            Dear %s,
            
            We have a new %s update for you:
            
            %s
            
            This message was sent on %s.
            
            Best regards,
            Guacom Team
            """,
                user.getName(),
                message.getCategory().getDisplayName(),
                message.getContent(),
                message.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String generateSubject(Message message) {
        return String.format("[%s] New Update Available", message.getCategory().getDisplayName());
    }

    private String simulateEmailDelivery(String email, String subject, String body) {
        String externalId = "EMAIL-" + UUID.randomUUID().toString().substring(0, 8);

        logger.info("📧 EMAIL SIMULATION 📧");
        logger.info(String.format("To: %s", email));
        logger.info(String.format("Subject: %s", subject));
        logger.info(String.format("Body: %s", body.substring(0, Math.min(100, body.length())) + "..."));
        logger.info(String.format("External ID: %s", externalId));
        logger.info("Email delivered successfully (simulated)");

        return externalId;
    }
}
