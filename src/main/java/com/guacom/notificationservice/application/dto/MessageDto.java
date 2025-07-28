package com.guacom.notificationservice.application.dto;

import com.guacom.notificationservice.domain.enums.MessageCategory;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String id;
    private MessageCategory category;
    private String content;
    private LocalDateTime createdAt;
}
