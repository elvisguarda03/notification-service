package com.guacom.notificationservice.infrastructure.channels;

import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.exceptions.NotificationException;
import com.guacom.notificationservice.domain.interfaces.ChannelFactory;
import com.guacom.notificationservice.domain.interfaces.NotificationChannelStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class NotificationChannelFactory implements ChannelFactory {
    private final Map<NotificationChannel, NotificationChannelStrategy> strategies;

    public NotificationChannelFactory(List<NotificationChannelStrategy> channelStrategies) {
        this.strategies = new HashMap<>();
        channelStrategies.forEach(strategy -> strategies.put(strategy.getChannelType(), strategy));
    }

    public NotificationChannelStrategy getNotificationChannelStrategy(NotificationChannel channel) {
        NotificationChannelStrategy strategy = strategies.get(channel);
        if (Objects.isNull(strategy)) {
            throw new NotificationException("No strategy found for channel: " + channel);
        }
        return strategy;
    }
}
