package com.api_3.api_3.service.notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Notification.Type;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.security.CurrentUserService;

@Service
public class TaskNotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private CurrentUserService currentUserService;
    // Using direct recipient computation and base builder for task notifications

    // Public API for task events (delegated from NotificationService facade)
    public void notifyTaskCreated(Task task) { notifyProjectMembers(task, Type.TASK_CREATED, "Tarefa criada", false); }
    public void notifyTaskUpdatedBroadcast(Task task) { notifyProjectMembers(task, Type.TASK_UPDATED, "Tarefa atualizada", false); }
    public void notifyTaskUpdatedScoped(Task task) {
        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            notifyOnlyUser(task, task.getResponsible().uuid(), Type.TASK_UPDATED, "Tarefa atualizada");
        } else {
            notifyProjectMembers(task, Type.TASK_UPDATED, "Tarefa atualizada", false);
        }
    }
    public void notifyTaskDeletedScoped(Task task) {
        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            notifyOnlyUser(task, task.getResponsible().uuid(), Type.TASK_DELETED, "Tarefa excluída");
        } else {
            notifyProjectMembers(task, Type.TASK_DELETED, "Tarefa excluída", false);
        }
    }

    public void notifyTaskAssigned(Task task, String newResponsibleUuid) {
        if (newResponsibleUuid == null) return;
        String actorUuid = currentUserService.currentUserUuid();
        if (actorUuid != null && actorUuid.equals(newResponsibleUuid)) return;
        notifyOnlyUser(task, newResponsibleUuid, Type.TASK_ASSIGNED, "Você foi atribuído(a) à tarefa");
    }

    public void notifyTaskUnassigned(Task task, String oldResponsibleUuid) {
        if (oldResponsibleUuid == null) return;
        String actorUuid = currentUserService.currentUserUuid();
        if (actorUuid != null && actorUuid.equals(oldResponsibleUuid)) return;
        notifyOnlyUser(task, oldResponsibleUuid, Type.TASK_UNASSIGNED, "Você foi removido(a) da tarefa");
    }

    public void notifyTaskComment(Task task, String authorUuid) {
        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            String r = task.getResponsible().uuid();
            if (r.equals(authorUuid)) return;
            notifyOnlyUser(task, r, Type.TASK_COMMENT, "Novo comentário na tarefa");
        } else {
            notifyProjectMembers(task, Type.TASK_COMMENT, "Novo comentário na tarefa", false);
        }
    }

    public void notifyTaskDueSoon(Task task) {
        if (task == null || task.getResponsible() == null || task.getResponsible().uuid() == null) return;
        Notification n = base(task, Type.TASK_DUE_SOON, "Tarefa vence em 1 dia");
        n.setUserUuid(task.getResponsible().uuid());
        notificationRepository.save(n);
    }

    // ---- helpers (task-scoped) ----
    private void notifyProjectMembers(Task task, Type type, String defaultMessage, boolean includeActor) {
        if (task.getProjectUuid() == null) return;
        Projects project = projectsRepository.findById(task.getProjectUuid()).orElse(null);
        if (project == null || project.getMembers() == null || project.getMembers().isEmpty()) return;
        String actorUuid = currentUserService.currentUserUuid();
        Set<String> recipients = project.getMembers().stream()
            .map(com.api_3.api_3.model.entity.User.UserRef::uuid)
            .filter(u -> u != null && (includeActor || !u.equals(actorUuid)))
            .collect(Collectors.toCollection(HashSet::new));

        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            String r = task.getResponsible().uuid();
            if (!r.equals(actorUuid)) recipients.add(r);
        }

        if (recipients.isEmpty()) return;

        createAndSaveNotifications(task, type, defaultMessage, recipients, actorUuid);
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

    private void notifyOnlyUser(Task task, String userUuid, Type type, String message) {
        Notification n = base(task, type, message);
        n.setUserUuid(userUuid);
        n.setActorUuid(currentUserService.currentUserUuid());
        notificationRepository.save(n);
    }

    private void createAndSaveNotifications(Task task, Type type, String message, Set<String> recipients, String actorUuid) {
        List<Notification> toSave = new ArrayList<>();
        for (String dest : recipients) {
            Notification n = base(task, type, message);
            n.setUserUuid(dest);
            n.setActorUuid(actorUuid);
            toSave.add(n);
        }
        if (!toSave.isEmpty()) notificationRepository.saveAll(toSave);
    }
}
