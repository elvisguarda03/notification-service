package com.guacom.notificationservice.domain.interfaces;

import com.guacom.notificationservice.domain.enums.NotificationChannel;

public interface ChannelFactory {
    NotificationChannelStrategy getNotificationChannelStrategy(NotificationChannel channel);
}
