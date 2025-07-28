package com.guacom.notificationservice.domain.entities;

import com.guacom.notificationservice.domain.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class NotificationResult {
    private final boolean success;
    private final NotificationStatus status;
    private final String externalMessageId;
    private final String errorMessage;
    private final LocalDateTime timestamp;

    private NotificationResult(boolean success, NotificationStatus status,
                               String externalMessageId, String errorMessage) {
        this.success = success;
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.externalMessageId = externalMessageId;
        this.errorMessage = errorMessage;
        this.timestamp = LocalDateTime.now();
    }

    public static NotificationResult success(String externalMessageId) {
        return new NotificationResult(true, NotificationStatus.SENT, externalMessageId, null);
    }

    public static NotificationResult delivered(String externalMessageId) {
        return new NotificationResult(true, NotificationStatus.DELIVERED, externalMessageId, null);
    }

    public static NotificationResult failure(String errorMessage) {
        return new NotificationResult(false, NotificationStatus.FAILED, null, errorMessage);
    }

    public static NotificationResult pending() {
        return new NotificationResult(false, NotificationStatus.PENDING, null, null);
    }

    // Getters
    public boolean isSuccess() { return success; }
    public NotificationStatus getStatus() { return status; }
    public String getExternalMessageId() { return externalMessageId; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("NotificationResult{success=%s, status=%s, externalId='%s', error='%s'}",
                success, status, externalMessageId, errorMessage);
    }
}
