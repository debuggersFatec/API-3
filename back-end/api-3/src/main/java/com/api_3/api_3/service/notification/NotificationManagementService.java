package com.api_3.api_3.service.notification;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.repository.NotificationRepository;

@Service
public class NotificationManagementService {

    @Autowired private NotificationRepository notificationRepository;

    public Optional<Notification> markRead(String id) {
        return notificationRepository.findById(id).map(n -> {
            n.setRead(true);
            n.setReadAt(new Date());
            return notificationRepository.save(n);
        });
    }

    public void delete(String id) {
        notificationRepository.deleteById(id);
    }
}
