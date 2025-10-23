package com.api_3.api_3.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Notification.Type;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;

    // -------- Public API for task events --------
    public void notifyTaskCreated(Task task) { notifyProjectMembers(task, Type.TASK_CREATED, "Tarefa criada"); }
    public void notifyTaskUpdated(Task task) { notifyProjectMembers(task, Type.TASK_UPDATED, "Tarefa atualizada"); }
    public void notifyTaskDeleted(Task task) { notifyProjectMembers(task, Type.TASK_DELETED, "Tarefa excluída"); }

    // Assignment specific notifications (to the target user only)
    public void notifyTaskAssigned(Task task, String newResponsibleUuid) {
        if (newResponsibleUuid == null) return;
        String actorUuid = getCurrentUserUuid();
        if (actorUuid != null && actorUuid.equals(newResponsibleUuid)) return; // não notifica auto-atribuição
        Notification n = base(task, Type.TASK_ASSIGNED, "Você foi atribuído(a) à tarefa");
        n.setUserUuid(newResponsibleUuid);
        n.setActorUuid(actorUuid);
        notificationRepository.save(n);
    }

    public void notifyTaskUnassigned(Task task, String oldResponsibleUuid) {
        if (oldResponsibleUuid == null) return;
        String actorUuid = getCurrentUserUuid();
        if (actorUuid != null && actorUuid.equals(oldResponsibleUuid)) return; // não notifica auto-desatribuição
        Notification n = base(task, Type.TASK_UNASSIGNED, "Você foi removido(a) da tarefa");
        n.setUserUuid(oldResponsibleUuid);
        n.setActorUuid(actorUuid);
        notificationRepository.save(n);
    }

    // Due soon scanner runs hourly and warns the responsible 1 day before
    @Scheduled(cron = "0 0 * * * *") // every hour
    public void scanDueSoon() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Date start = Date.from(now.toInstant());
        Date end = Date.from(now.plusDays(1).toInstant());
        List<Task> tasks = taskRepository.findByDueDateBetweenAndStatusNot(start, end, Task.Status.COMPLETED);
        if (tasks == null || tasks.isEmpty()) return;

        // Notify only once per task per day
        Date todayStart = Date.from(now.toLocalDate().atStartOfDay(now.getZone()).toInstant());

        for (Task task : tasks) {
            if (task.getResponsible() == null || task.getResponsible().uuid() == null) continue;
            Optional<Notification> existing = notificationRepository.findFirstByTypeAndTaskUuidAndCreatedAtAfter(
                Type.TASK_DUE_SOON, task.getUuid(), todayStart
            );
            if (existing.isPresent()) continue;

            Notification n = base(task, Type.TASK_DUE_SOON, "Tarefa vence em 1 dia");
            n.setUserUuid(task.getResponsible().uuid());
            notificationRepository.save(n);
        }
    }

    // -------- Queries and updates for controllers --------
    public List<Notification> listForCurrentUser(boolean unreadOnly) {
        String userUuid = getCurrentUserUuid();
        if (userUuid == null) return List.of();
        return unreadOnly
            ? notificationRepository.findByUserUuidAndReadFalseOrderByCreatedAtDesc(userUuid)
            : notificationRepository.findByUserUuidOrderByCreatedAtDesc(userUuid);
    }

    public Optional<Notification> markRead(String id) {
        return notificationRepository.findById(id).map(n -> {
            n.setRead(true);
            n.setReadAt(new Date());
            return notificationRepository.save(n);
        });
    }

    public void delete(String id) { notificationRepository.deleteById(id); }

    // -------- Internal helpers --------
    private void notifyProjectMembers(Task task, Type type, String defaultMessage) {
        if (task.getProjectUuid() == null) return;
        Projects project = projectsRepository.findById(task.getProjectUuid()).orElse(null);
        if (project == null || project.getMembers() == null || project.getMembers().isEmpty()) return;

        String actorUuid = getCurrentUserUuid();
        Set<String> recipients = project.getMembers().stream()
            .map(User.UserRef::uuid)
            .filter(u -> u != null && !u.equals(actorUuid))
            .collect(Collectors.toCollection(HashSet::new));

        // Also ensure responsible is notified (if present and not the actor)
        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            String r = task.getResponsible().uuid();
            if (!r.equals(actorUuid)) recipients.add(r);
        }

        if (recipients.isEmpty()) return;

        List<Notification> toSave = new ArrayList<>();
        for (String dest : recipients) {
            Notification n = base(task, type, defaultMessage);
            n.setUserUuid(dest);
            n.setActorUuid(actorUuid);
            toSave.add(n);
        }
        notificationRepository.saveAll(toSave);
    }

    private Notification base(Task task, Type type, String message) {
        Notification n = new Notification();
        n.setType(type);
        n.setTaskUuid(task.getUuid());
        n.setTaskTitle(task.getTitle());
        n.setProjectUuid(task.getProjectUuid());
        n.setTeamUuid(task.getEquip_uuid());
        n.setMessage(message);
        n.setCreatedAt(Date.from(Instant.now()));
        return n;
    }

    private String getCurrentUserUuid() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) return null;
            return userRepository.findByEmailIgnoreCase(auth.getName())
                    .map(User::getUuid)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
