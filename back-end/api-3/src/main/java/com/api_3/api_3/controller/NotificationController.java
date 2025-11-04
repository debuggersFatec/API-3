package com.api_3.api_3.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.service.notification.NotificationManagementService;
import com.api_3.api_3.service.notification.NotificationQueryService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired private NotificationQueryService notificationQueryService;
    @Autowired private NotificationManagementService notificationManagementService;

    @GetMapping
    // GET /api/notifications?unreadOnly=true|false -> lista notificações do usuário
    public List<Notification> list(@RequestParam(name = "unreadOnly", required = false, defaultValue = "false") boolean unreadOnly) {
        return notificationQueryService.listForCurrentUser(unreadOnly);
    }

    @PostMapping("/{id}/read")
    // POST /api/notifications/{id}/read -> marca como lida
    public ResponseEntity<Notification> markRead(@PathVariable String id) {
        return notificationManagementService.markRead(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    // DELETE /api/notifications/{id} -> apaga notificação
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
