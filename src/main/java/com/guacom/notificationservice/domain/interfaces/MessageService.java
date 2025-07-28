package com.guacom.notificationservice.domain.interfaces;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.domain.enums.MessageCategory;

import java.util.List;

public interface MessageService {
    List<NotificationLogDto> processMessage(MessageCategory category, String content);
}
