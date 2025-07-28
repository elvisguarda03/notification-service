package com.guacom.notificationservice.application.services;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationLog;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.enums.NotificationStatus;
import com.guacom.notificationservice.domain.interfaces.ChannelFactory;
import com.guacom.notificationservice.domain.interfaces.NotificationRepository;
import com.guacom.notificationservice.domain.interfaces.NotificationService;
import com.guacom.notificationservice.domain.interfaces.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = Logger.getLogger(NotificationServiceImpl.class.getName());

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ChannelFactory channelFactory;

    public NotificationServiceImpl(UserRepository userRepository,
                                   NotificationRepository notificationRepository,
                                   ChannelFactory channelFactory) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.channelFactory = channelFactory;
    }

    public List<NotificationLogDto> sendNotifications(Message message) {
        logger.info(String.format("Starting notification process for message: %s", message.getId()));

        List<User> subscribedUsers = userRepository.findBySubscribedCategory(message.getCategory());
        logger.info(String.format("Found %d users subscribed to category %s",
                subscribedUsers.size(), message.getCategory()));

        var results = subscribedUsers.stream()
                .map(user -> sendNotificationsToUser(user, message))
                .flatMap(List::stream)
                .toList();

        logger.info(String.format("Notification process completed. Total notifications sent: %d", results.size()));
        return results;
    }

    public List<NotificationLogDto> sendNotificationsToUser(User user, Message message) {
        return user.getPreferredChannels().stream()
                .filter(channel -> user.canReceiveNotification(message.getCategory(), channel))
                .map(channel -> sendSingleNotification(user, message, channel))
                .toList();
    }

    public NotificationLogDto sendSingleNotification(User user, Message message, NotificationChannel channel) {
        logger.info(String.format("Sending %s notification to user %s", channel, user.getName()));

        try {
            var notificationChannelStrategy = channelFactory.getNotificationChannelStrategy(channel);
            var result = notificationChannelStrategy.sendNotification(user, message);

            NotificationLog log = NotificationLog.builder()
                    .id(UUID.randomUUID().toString())
                    .messageId(message.getId())
                    .userId(user.getId())
                    .userName(user.getName())
                    .userEmail(user.getEmail())
                    .userPhone(user.getPhoneNumber())
                    .messageCategory(message.getCategory())
                    .messageContent(message.getContent())
                    .channel(channel)
                    .status(result.getStatus())
                    .sentAt(LocalDateTime.now())
                    .deliveredAt(result.isSuccess() ? result.getTimestamp() : null)
                    .errorMessage(result.getErrorMessage())
                    .externalMessageId(result.getExternalMessageId())
                    .build();

            notificationRepository.save(log);

            logger.info(String.format("Notification %s: %s",
                    result.isSuccess() ? "sent successfully" : "failed", log.getId()));

            return convertToDto(log);

        } catch (Exception e) {
            logger.severe(String.format("Error sending notification to user %s via %s: %s",
                    user.getName(), channel, e.getMessage()));

            NotificationLog failedLog = NotificationLog.builder()
                    .messageId(message.getId())
                    .userId(user.getId())
                    .userName(user.getName())
                    .userEmail(user.getEmail())
                    .userPhone(user.getPhoneNumber())
                    .messageCategory(message.getCategory())
                    .messageContent(message.getContent())
                    .channel(channel)
                    .status(NotificationStatus.FAILED)
                    .sentAt(LocalDateTime.now())
                    .errorMessage("System error: " + e.getMessage())
                    .build();

            notificationRepository.save(failedLog);
            return convertToDto(failedLog);
        }
    }

    public List<NotificationLogDto> getNotificationHistory() {
        List<NotificationLog> logs = notificationRepository.findAllOrderByCreatedDateDesc();
        return logs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private NotificationLogDto convertToDto(NotificationLog log) {
        return NotificationLogDto.builder()
                .id(log.getId())
                .messageId(log.getMessageId())
                .userId(log.getUserId())
                .userName(log.getUserName())
                .userEmail(log.getUserEmail())
                .userPhone(log.getUserPhone())
                .messageCategory(log.getMessageCategory())
                .messageContent(log.getMessageContent())
                .channel(log.getChannel())
                .status(log.getStatus())
                .sentAt(log.getSentAt())
                .deliveredAt(log.getDeliveredAt())
                .errorMessage(log.getErrorMessage())
                .externalMessageId(log.getExternalMessageId())
                .build();
    }
}
