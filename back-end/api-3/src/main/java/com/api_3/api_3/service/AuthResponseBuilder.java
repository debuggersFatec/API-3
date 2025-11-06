package com.api_3.api_3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.repository.NotificationRepository;

@Service
public class AuthResponseBuilder {

    @Autowired
    private NotificationRepository notificationRepository;

    public AuthResponse.Routes defaultRoutes() {
        return new AuthResponse.Routes(
            "/api/teams",
            "/api/projects",
            "/api/teams/{teamUuid}/members",
            "/api/tasks"
        );
    }

    public long unreadCount(String userUuid) {
        if (userUuid == null) return 0L;
        List<Notification> all = notificationRepository.findByUserUuidOrderByCreatedAtDesc(userUuid);
        return all.stream().filter(n -> !n.isRead()).count();
    }

    public List<AuthResponse.NotificationInfo> recentNotifications(String userUuid) {
        if (userUuid == null) return List.of();
        List<Notification> all = notificationRepository.findByUserUuidOrderByCreatedAtDesc(userUuid);
        return all.stream().limit(20)
            .map(n -> new AuthResponse.NotificationInfo(
                n.getId(),
                n.getType() != null ? n.getType().name() : null,
                n.getMessage(),
                n.getTaskUuid(),
                n.getTaskTitle(),
                n.getProjectUuid(),
                n.getTeamUuid(),
                n.getActorUuid(),
                n.getCreatedAt(),
                n.isRead()
            ))
            .collect(Collectors.toList());
    }

    public AuthResponse build(String token, AuthResponse.UserInfo userInfo, String userUuid) {
        AuthResponse.Routes routes = defaultRoutes();
        long unread = unreadCount(userUuid);
        List<AuthResponse.NotificationInfo> recent = recentNotifications(userUuid);

        // Attach notifications into the user object
        userInfo.setNotificationsUnread(unread);
        userInfo.setNotificationsRecent(recent);

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setRoutes(routes);
        resp.setUser(userInfo);
        return resp;
    }
}
