package com.guacom.notificationservice.presentation.controllers;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.application.services.MessageServiceImpl;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.presentation.dto.ApiResponse;
import com.guacom.notificationservice.presentation.dto.CreateMessageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private static final Logger logger = Logger.getLogger(MessageController.class.getName());

    private final MessageServiceImpl messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<NotificationLogDto>>> sendMessage(
            @Valid @RequestBody CreateMessageRequest request) {

        logger.info(String.format("Processing message send request for category: %s", request.getCategory()));

        List<NotificationLogDto> results = messageService.processMessage(
                request.getCategory(),
                request.getContent()
        );

        logger.info(String.format("Message processed successfully. Sent %d notifications", results.size()));

        ApiResponse<List<NotificationLogDto>> response = ApiResponse.success(
                String.format("Message processed successfully. Sent %d notifications.", results.size()),
                results
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<MessageCategory[]>> getCategories() {
        logger.info("Retrieving message categories");

        MessageCategory[] categories = MessageCategory.values();

        ApiResponse<MessageCategory[]> response = ApiResponse.success(
                "Categories retrieved successfully",
                categories
        );

        return ResponseEntity.ok(response);
    }
}
