package com.api_3.auth_service.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api_3.auth_service.model.entity.Notification;
import com.api_3.auth_service.model.entity.Notification.Type;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserUuidOrderByCreatedAtDesc(String userUuid);
    List<Notification> findByUserUuidAndReadFalseOrderByCreatedAtDesc(String userUuid);

    long countByUserUuidAndReadFalse(String userUuid);
    List<Notification> findTop20ByUserUuidOrderByCreatedAtDesc(String userUuid);

    Optional<Notification> findFirstByTypeAndTaskUuidAndCreatedAtAfter(Type type, String taskUuid, Date after);
}
