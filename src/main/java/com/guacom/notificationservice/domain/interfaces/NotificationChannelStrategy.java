package com.guacom.notificationservice.domain.interfaces;

import com.guacom.notificationservice.domain.entities.Message;
import com.guacom.notificationservice.domain.entities.NotificationResult;
import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.NotificationChannel;

public interface NotificationChannelStrategy {
    NotificationChannel getChannelType();
    NotificationResult sendNotification(User user, Message message);
    boolean validateRecipient(User user);
    String formatMessage(Message message, User user);
}
