package com.guacom.notificationservice.application.services;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationLog;
import com.guacom.notificationservice.domain.entities.NotificationResult;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.enums.NotificationStatus;
import com.guacom.notificationservice.domain.interfaces.ChannelFactory;
import com.guacom.notificationservice.domain.interfaces.NotificationChannelStrategy;
import com.guacom.notificationservice.domain.interfaces.NotificationRepository;
import com.guacom.notificationservice.domain.interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ChannelFactory channelFactory;

    @Mock
    private NotificationChannelStrategy emailStrategy;

    @Mock
    private NotificationChannelStrategy smsStrategy;

    private NotificationServiceImpl notificationService;

    private User testUser;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(userRepository, notificationRepository, channelFactory);

        testUser = new User(
                "user-1",
                "John Doe",
                "john.doe@email.com",
                "+1234567890",
                Arrays.asList(MessageCategory.SPORTS, MessageCategory.FINANCE),
                Arrays.asList(NotificationChannel.EMAIL, NotificationChannel.SMS)
        );

        testMessage = Message.builder()
                .category(MessageCategory.SPORTS)
                .content("Important sports update!")
                .build();
    }

    @Test
    void sendNotifications_ShouldSendToAllSubscribedUsers() {
        // Arrange
        List<User> subscribedUsers = Collections.singletonList(testUser);
        when(userRepository.findBySubscribedCategory(MessageCategory.SPORTS)).thenReturn(subscribedUsers);
        when(channelFactory.getNotificationChannelStrategy(NotificationChannel.EMAIL)).thenReturn(emailStrategy);
        when(channelFactory.getNotificationChannelStrategy(NotificationChannel.SMS)).thenReturn(smsStrategy);
        when(emailStrategy.sendNotification(testUser, testMessage)).thenReturn(NotificationResult.success("email-123"));
        when(smsStrategy.sendNotification(testUser, testMessage)).thenReturn(NotificationResult.success("sms-456"));

        // Act
        List<NotificationLogDto> results = notificationService.sendNotifications(testMessage);

        // Assert
        assertEquals(2, results.size());
        verify(userRepository).findBySubscribedCategory(MessageCategory.SPORTS);
        verify(channelFactory, times(2)).getNotificationChannelStrategy(any(NotificationChannel.class));
        verify(emailStrategy).sendNotification(testUser, testMessage);
        verify(smsStrategy).sendNotification(testUser, testMessage);
        verify(notificationRepository, times(2)).save(any(NotificationLog.class));
    }

    @Test
    void sendSingleNotification_ShouldHandleSuccessfulDelivery() {
        // Arrange
        when(channelFactory.getNotificationChannelStrategy(NotificationChannel.EMAIL)).thenReturn(emailStrategy);
        when(emailStrategy.sendNotification(testUser, testMessage)).thenReturn(NotificationResult.success("email-123"));

        // Act
        NotificationLogDto result = notificationService.sendSingleNotification(testUser, testMessage, NotificationChannel.EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(NotificationStatus.SENT, result.getStatus());
    }

    @Test
    void getNotificationHistory_ShouldReturnAllNotificationsAsDto() {
        // Arrange
        List<NotificationLog> mockLogs = List.of(
                createMockNotificationLog(UUID.randomUUID(), UUID.randomUUID(), NotificationChannel.EMAIL, NotificationStatus.SENT),
                createMockNotificationLog(UUID.randomUUID(), UUID.randomUUID(), NotificationChannel.SMS, NotificationStatus.FAILED),
                createMockNotificationLog(UUID.randomUUID(), UUID.randomUUID(), NotificationChannel.PUSH, NotificationStatus.SENT)
        );

        when(notificationRepository.findAllOrderByCreatedDateDesc()).thenReturn(mockLogs);

        // Act
        List<NotificationLogDto> result = notificationService.getNotificationHistory();

        // Assert
        assertNotNull(result);
        assertEquals(mockLogs.size(), result.size());


        for (int i = 0; i < mockLogs.size(); i++) {
            NotificationLog log = mockLogs.get(i);
            NotificationLogDto dto = result.get(i);

            assertThat(dto)
                    .extracting(
                            NotificationLogDto::getId,
                            NotificationLogDto::getUserId,
                            NotificationLogDto::getMessageId,
                            NotificationLogDto::getChannel,
                            NotificationLogDto::getStatus
                    )
                    .containsExactly(
                            log.getId(),
                            log.getUserId(),
                            log.getMessageId(),
                            log.getChannel(),
                            log.getStatus()
                    );
        }


        verify(notificationRepository).findAllOrderByCreatedDateDesc();
    }

    private NotificationLog createMockNotificationLog(UUID userId, UUID messageId,
                                                      NotificationChannel channel,
                                                      NotificationStatus status) {
        NotificationLog log = new NotificationLog();
        log.setId(UUID.randomUUID().toString());
        log.setUserId(userId.toString());
        log.setMessageId(messageId.toString());
        log.setChannel(channel);
        log.setStatus(status);
        return log;
    }


    @Test
    void sendSingleNotification_ShouldHandleExceptionDuringDelivery() {
        // Arrange
        User user = User.builder().build();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("test@example.com");
        user.setPhoneNumber("123456789");

        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setContent("Test message content");

        NotificationChannel channel = NotificationChannel.EMAIL;

        when(channelFactory.getNotificationChannelStrategy(NotificationChannel.EMAIL)).thenReturn(emailStrategy);
        doThrow(new RuntimeException("Failed to send notification"))
                .when(emailStrategy).sendNotification(any(), any());

        // Act
        NotificationLogDto result = notificationService.sendSingleNotification(user, message, channel);

        // Assert
        assertNotNull(result);
        assertEquals(NotificationStatus.FAILED, result.getStatus());
        assertEquals(channel, result.getChannel());

        ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationRepository).save(logCaptor.capture());

        NotificationLog savedLog = logCaptor.getValue();
        assertEquals(NotificationStatus.FAILED, savedLog.getStatus());
        assertEquals(user.getId(), savedLog.getUserId());
        assertEquals(message.getId(), savedLog.getMessageId());
        assertEquals(channel, savedLog.getChannel());
    }
}
