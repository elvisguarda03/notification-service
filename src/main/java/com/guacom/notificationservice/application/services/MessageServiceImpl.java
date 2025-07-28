package com.guacom.notificationservice.application.services;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.application.validators.MessageValidator;
import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.interfaces.MessageService;
import com.guacom.notificationservice.domain.interfaces.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = Logger.getLogger(MessageServiceImpl.class.getName());

    private final MessageValidator messageValidator;
    private final NotificationService notificationService;

    public MessageServiceImpl(MessageValidator messageValidator, NotificationService notificationService) {
        this.messageValidator = messageValidator;
        this.notificationService = notificationService;
    }

    public List<NotificationLogDto> processMessage(MessageCategory category, String content) {
        logger.info(String.format("Processing message for category: %s", category));

        String sanitizedContent = messageValidator.sanitizeContent(content);

        Message message = Message.builder()
                .category(category)
                .content(sanitizedContent)
                .build();
        logger.info(String.format("Created message with ID: %s", message.getId()));

        List<NotificationLogDto> results = notificationService.sendNotifications(message);

        logger.info(String.format("Message processing completed. Sent %d notifications", results.size()));
        return results;
    }
}
