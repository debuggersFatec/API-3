package com.api_3.api_3.service.notification;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.dto.response.NotificationDto;
import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.security.CurrentUserService;

@Service
public class NotificationQueryService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private CurrentUserService currentUserService;

    public List<Notification> listForCurrentUser(boolean unreadOnly) {
        String userUuid = currentUserService.currentUserUuid();
        if (userUuid == null) return List.of();
        return unreadOnly
            ? notificationRepository.findByUserUuidAndReadFalseOrderByCreatedAtDesc(userUuid)
            : notificationRepository.findByUserUuidOrderByCreatedAtDesc(userUuid);
    }

    public long countUnreadByUserUuid(String userUuid) {
        if (userUuid == null) return 0L;
        return notificationRepository.countByUserUuidAndReadFalse(userUuid);
    }

    public List<NotificationDto> findRecentTop20DtosByUserUuid(String userUuid) {
        if (userUuid == null) return List.of();
        return notificationRepository
            .findTop20ByUserUuidOrderByCreatedAtDesc(userUuid)
            .stream()
            .map(n -> new NotificationDto(
                n.getId(),
                n.getType() != null ? n.getType().name() : null,
                n.getTeamUuid(),
                n.getProjectUuid(),
                n.getTaskUuid(),
                n.getTaskTitle(),
                n.getMessage(),
                n.isRead(),
                n.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }
}
