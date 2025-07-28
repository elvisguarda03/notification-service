package com.guacom.notificationservice.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.presentation.dto.CreateMessageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class MessageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendMessage_ShouldReturnSuccess_WithValidRequest() throws Exception {
        // Arrange
        CreateMessageRequest request = new CreateMessageRequest(
                MessageCategory.SPORTS,
                "This is a valid sports message for integration testing"
        );

        // Act & Assert
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Message processed successfully. Sent 10 notifications."))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void sendMessage_ShouldReturnBadRequest_WithInvalidContent() throws Exception {
        // Arrange
        CreateMessageRequest request = new CreateMessageRequest(MessageCategory.SPORTS, "Short");

        // Act & Assert
        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Content must be between 10 and 1000 characters")));
    }

    @Test
    void getCategories_ShouldReturnAllCategories() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/messages/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }
}