package com.api_3.api_3.service.notification;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.api_3.api_3.dto.response.NotificationDto;
import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public long countUnreadByUserUuid(String userUuid) {
        if (userUuid == null || userUuid.isBlank()) return 0L;
        return notificationRepository.countByUserUuidAndReadFalse(userUuid);
    }

    @Override
    public List<NotificationDto> findRecentTop20DtosByUserUuid(String userUuid) {
        if (userUuid == null || userUuid.isBlank()) return List.of();
        return notificationRepository
            .findTop20ByUserUuidOrderByCreatedAtDesc(userUuid)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<Notification> listForCurrentUser(boolean unreadOnly) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return List.of();
        String email = auth.getName();
        String userUuid = userRepository.findByEmail(email).map(u -> u.getUuid()).orElse(null);
        if (userUuid == null) return List.of();
        return unreadOnly
            ? notificationRepository.findByUserUuidAndReadFalseOrderByCreatedAtDesc(userUuid)
            : notificationRepository.findByUserUuidOrderByCreatedAtDesc(userUuid);
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(
            n.getId(),
            n.getType() != null ? n.getType().name() : null,
            n.getTeamUuid(),
            n.getProjectUuid(),
            n.getTaskUuid(),
            n.getTaskTitle(),
            n.getMessage(),
            n.isRead(),
            n.getCreatedAt()
        );
    }
}
