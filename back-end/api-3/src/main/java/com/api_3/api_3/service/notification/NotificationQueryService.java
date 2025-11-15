package com.api_3.api_3.service.notification;

import java.util.List;

import com.api_3.api_3.dto.response.NotificationDto;
import com.api_3.api_3.model.entity.Notification;

public interface NotificationQueryService {
    long countUnreadByUserUuid(String userUuid);
    List<NotificationDto> findRecentTop20DtosByUserUuid(String userUuid);
    List<Notification> listForCurrentUser(boolean unreadOnly);
}
