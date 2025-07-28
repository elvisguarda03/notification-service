package com.guacom.notificationservice.domain.interfaces;

import com.guacom.notificationservice.domain.entities.User;
import com.guacom.notificationservice.domain.enums.MessageCategory;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findById(String id);

    List<User> findBySubscribedCategory(MessageCategory category);

    void save(User user);

    void deleteById(String id);

    long count();
}
