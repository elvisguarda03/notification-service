package com.guacom.notificationservice.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    PENDING("Pending"),
    SENT("Sent"),
    DELIVERED("Delivered"),
    FAILED("Failed"),
    RETRYING("Retrying");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
