package com.guacom.notificationservice.domain.entities;

import com.guacom.notificationservice.domain.enums.MessageCategory;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private MessageCategory category;
    private String content;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
