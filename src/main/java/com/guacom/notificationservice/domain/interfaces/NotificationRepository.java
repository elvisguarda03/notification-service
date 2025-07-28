package com.guacom.notificationservice.domain.interfaces;

import com.guacom.notificationservice.domain.entities.NotificationLog;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    void save(NotificationLog log);

    Optional<NotificationLog> findById(String id);

    List<NotificationLog> findAll();

    List<NotificationLog> findAllOrderByCreatedDateDesc();

    List<NotificationLog> findByUserId(String userId);
}
