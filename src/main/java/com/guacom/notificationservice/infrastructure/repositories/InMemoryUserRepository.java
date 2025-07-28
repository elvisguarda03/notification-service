package com.guacom.notificationservice.infrastructure.repositories;

import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.MessageCategory;
import com.guacom.notificationservice.domain.enums.NotificationChannel;
import com.guacom.notificationservice.domain.interfaces.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeData() {
        // Pre-populate with mock users
        List<User> mockUsers = createMockUsers();
        mockUsers.forEach(user -> users.put(user.getId(), user));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findBySubscribedCategory(MessageCategory category) {
        return users.values().stream()
                .filter(user -> user.isSubscribedToCategory(category))
                .collect(Collectors.toList());
    }

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void deleteById(String id) {
        users.remove(id);
    }

    @Override
    public long count() {
        return users.size();
    }

    private List<User> createMockUsers() {
        return Arrays.asList(
                new User("user-1", "John Smith", "john.smith@email.com", "+1-555-0101",
                        Arrays.asList(MessageCategory.SPORTS, MessageCategory.FINANCE),
                        Arrays.asList(NotificationChannel.EMAIL, NotificationChannel.SMS)),

                new User("user-2", "Alice Johnson", "alice.johnson@email.com", "+1-555-0102",
                        Arrays.asList(MessageCategory.MOVIES, MessageCategory.SPORTS),
                        Arrays.asList(NotificationChannel.EMAIL, NotificationChannel.PUSH)),

                new User("user-3", "Bob Wilson", "bob.wilson@email.com", "+1-555-0103",
                        List.of(MessageCategory.FINANCE),
                        List.of(NotificationChannel.SMS)),

                new User("user-4", "Carol Davis", "carol.davis@email.com", "+1-555-0104",
                        Arrays.asList(MessageCategory.MOVIES, MessageCategory.FINANCE, MessageCategory.SPORTS),
                        Arrays.asList(NotificationChannel.EMAIL, NotificationChannel.SMS, NotificationChannel.PUSH)),

                new User("user-5", "David Brown", "david.brown@email.com", "+1-555-0105",
                        List.of(MessageCategory.SPORTS),
                        List.of(NotificationChannel.PUSH)),

                new User("user-6", "Emma Taylor", "emma.taylor@email.com", "+1-555-0106",
                        List.of(MessageCategory.MOVIES),
                        List.of(NotificationChannel.EMAIL)),

                new User("user-7", "Frank Miller", "frank.miller@email.com", "+1-555-0107",
                        Arrays.asList(MessageCategory.FINANCE, MessageCategory.SPORTS),
                        Arrays.asList(NotificationChannel.SMS, NotificationChannel.PUSH)),

                new User("user-8", "Grace Lee", "grace.lee@email.com", "+1-555-0108",
                        Arrays.asList(MessageCategory.MOVIES, MessageCategory.FINANCE),
                        Arrays.asList(NotificationChannel.EMAIL, NotificationChannel.PUSH))
        );
    }
}
