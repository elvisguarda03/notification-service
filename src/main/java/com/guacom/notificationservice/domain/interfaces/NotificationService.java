package com.guacom.notificationservice.domain.interfaces;

import com.guacom.notificationservice.application.dto.NotificationLogDto;
import com.guacom.notificationservice.domain.entities.Message;

import java.util.List;

public interface NotificationService {

    List<NotificationLogDto> getNotificationHistory();

    List<NotificationLogDto> sendNotifications(Message message);
}
