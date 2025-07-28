package com.guacom.notificationservice.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.application.services.NotificationServiceImpl;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.enums.NotificationStatus;
import com.guacom.notificationservice.domain.interfaces.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationServiceImpl notificationService;

    @Test
    void getNotificationHistory_ShouldReturnAllNotifications() throws Exception {
        // Arrange
        List<NotificationLogDto> mockHistory = Arrays.asList(
                createMockNotificationLog("user1@example.com", NotificationChannel.EMAIL, true),
                createMockNotificationLog("user2@example.com", NotificationChannel.SMS, true),
                createMockNotificationLog("user3@example.com", NotificationChannel.PUSH, false)
        );

        when(notificationService.getNotificationHistory()).thenReturn(mockHistory);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Retrieved 3 notification records"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].recipient").value("user1@example.com"))
                .andExpect(jsonPath("$.data[0].channel").value("EMAIL"))
                .andExpect(jsonPath("$.data[0].successful").value(true))
                .andExpect(jsonPath("$.data[1].recipient").value("user2@example.com"))
                .andExpect(jsonPath("$.data[2].successful").value(false));
    }

    @Test
    void getNotificationHistory_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Arrange
        List<NotificationLogDto> emptyHistory = List.of();

        when(notificationService.getNotificationHistory()).thenReturn(emptyHistory);

        // Act & Assert
        mockMvc.perform(get("/api/notifications/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Retrieved 0 notification records"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // Helper method to create mock notification logs
    private NotificationLogDto createMockNotificationLog(
            String recipient,
            NotificationChannel channel,
            boolean successful) {

        return NotificationLogDto.builder()
                .id(UUID.randomUUID().toString())
                .userEmail(recipient)
                .channel(channel)
                .messageCategory(MessageCategory.SPORTS)
                .messageContent("Test notification content")
                .status(successful ? NotificationStatus.DELIVERED : NotificationStatus.FAILED)
                .errorMessage(successful ? null : "Failed to deliver")
                .sentAt(LocalDateTime.now())
                .build();
    }
}

