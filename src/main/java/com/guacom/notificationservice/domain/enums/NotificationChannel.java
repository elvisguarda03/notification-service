package com.guacom.notificationservice.domain.enums;

public enum NotificationChannel {
    SMS("SMS"),
    EMAIL("E-Mail"),
    PUSH("Push Notification");

    private final String displayName;

    NotificationChannel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
