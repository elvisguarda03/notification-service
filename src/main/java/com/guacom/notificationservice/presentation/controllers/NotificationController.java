package com.guacom.notificationservice.presentation.controllers;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.application.services.NotificationServiceImpl;
import com.guacom.notificationservice.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final Logger logger = Logger.getLogger(NotificationController.class.getName());

    private final NotificationServiceImpl notificationService;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<NotificationLogDto>>> getNotificationHistory() {
        logger.info("Retrieving notification history");

        List<NotificationLogDto> history = notificationService.getNotificationHistory();

        ApiResponse<List<NotificationLogDto>> response = ApiResponse.success(
                String.format("Retrieved %d notification records", history.size()),
                history
        );

        return ResponseEntity.ok(response);
    }
}