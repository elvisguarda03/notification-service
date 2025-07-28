package com.guacom.notificationservice.infrastructure.repositories;

import com.guacom.notificationservice.domain.entities.NotificationLog;
import com.guacom.notificationservice.domain.interfaces.NotificationRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<String, NotificationLog> notifications = new ConcurrentHashMap<>();

    @Override
    public void save(NotificationLog log) {
        notifications.put(log.getId(), log);
    }

    @Override
    public Optional<NotificationLog> findById(String id) {
        return Optional.ofNullable(notifications.get(id));
    }

    @Override
    public List<NotificationLog> findAll() {
        return new ArrayList<>(notifications.values());
    }

    @Override
    public List<NotificationLog> findAllOrderByCreatedDateDesc() {
        return notifications.values().stream()
                .sorted((a, b) -> b.getSentAt().compareTo(a.getSentAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationLog> findByUserId(String userId) {
        return notifications.values().stream()
                .filter(log -> log.getUserId().equals(userId))
                .sorted((a, b) -> b.getSentAt().compareTo(a.getSentAt()))
                .collect(Collectors.toList());
    }
}
