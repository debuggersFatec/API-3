package com.api_3.api_3.service.notification;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Notification.Type;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.service.NotificationService;

@Component
public class NotificationScheduler {

    @Autowired private TaskRepository taskRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private NotificationService notificationService;

    // every hour
    @Scheduled(cron = "0 0 * * * *")
    public void scanDueSoon() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Date start = Date.from(now.toInstant());
        Date end = Date.from(now.plusDays(1).toInstant());
        List<Task> tasks = taskRepository.findByDueDateBetweenAndStatusNot(start, end, Task.Status.COMPLETED);
        if (tasks == null || tasks.isEmpty()) return;

        Date todayStart = Date.from(now.toLocalDate().atStartOfDay(now.getZone()).toInstant());

        for (Task task : tasks) {
            if (task.getResponsible() == null || task.getResponsible().uuid() == null) continue;
            Optional<Notification> existing = notificationRepository.findFirstByTypeAndTaskUuidAndCreatedAtAfter(
                Type.TASK_DUE_SOON, task.getUuid(), todayStart
            );
            if (existing.isPresent()) continue;

            notificationService.notifyTaskDueSoon(task);
        }
    }
}
