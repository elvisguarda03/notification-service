package com.guacom.notificationservice.domain.enums;

public enum MessageCategory {
    SPORTS("Sports"),
    FINANCE("Finance"),
    MOVIES("Movies");

    private final String displayName;

    MessageCategory(String displayName) {
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