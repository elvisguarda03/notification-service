package com.guacom.notificationservice.domain.entities;

import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.enums.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {
    private String id;
    private String messageId;
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private MessageCategory messageCategory;
    private String messageContent;
    private NotificationChannel channel;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private String errorMessage;
    private String externalMessageId;
}
