package com.guacom.notificationservice.domain.entities;

import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private List<MessageCategory> subscribedCategories;
    private List<NotificationChannel> preferredChannels;

    public boolean isSubscribedToCategory(MessageCategory category) {
        return subscribedCategories.contains(category);
    }

    public boolean hasPreferredChannel(NotificationChannel channel) {
        return preferredChannels.contains(channel);
    }

    public boolean canReceiveNotification(MessageCategory category, NotificationChannel channel) {
        return isSubscribedToCategory(category) && hasPreferredChannel(channel);
    }
}