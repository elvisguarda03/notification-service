package com.guacom.notificationservice.application.services;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.application.validators.MessageValidator;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    private MessageValidator messageValidator;

    @Mock
    private NotificationService notificationService;

    private MessageServiceImpl messageService;

    @BeforeEach
    void setUp() {
        messageValidator = new MessageValidator();
        messageService = new MessageServiceImpl(messageValidator, notificationService);
    }

    @Test
    void processMessage_ShouldValidateAndSendNotifications() {
        // Arrange
        MessageCategory category = MessageCategory.SPORTS;
        String content = "Important sports news update";

        List<NotificationLogDto> expectedResults = Arrays.asList(
                createMockNotificationLogDto("log-1"),
                createMockNotificationLogDto("log-2")
        );

        when(notificationService.sendNotifications(any())).thenReturn(expectedResults);

        // Act
        List<NotificationLogDto> results = messageService.processMessage(category, content);

        // Assert
        assertEquals(2, results.size());
        verify(notificationService).sendNotifications(any());
    }

    private NotificationLogDto createMockNotificationLogDto(String id) {
        NotificationLogDto dto = new NotificationLogDto();
        dto.setId(id);
        return dto;
    }
}
