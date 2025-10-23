package com.api_3.api_3.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Notification.Type;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserUuidOrderByCreatedAtDesc(String userUuid);
    List<Notification> findByUserUuidAndReadFalseOrderByCreatedAtDesc(String userUuid);

    Optional<Notification> findFirstByTypeAndTaskUuidAndCreatedAtAfter(Type type, String taskUuid, Date after);
}
