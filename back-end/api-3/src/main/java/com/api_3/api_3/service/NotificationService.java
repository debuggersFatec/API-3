package com.api_3.api_3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Task;

@Service
public class NotificationService {

    @Autowired private com.api_3.api_3.service.notification.TaskNotificationService taskNotificationService;
    @Autowired private com.api_3.api_3.service.notification.ProjectNotificationService projectNotificationService;
    @Autowired private com.api_3.api_3.service.notification.TeamNotificationService teamNotificationService;
    @Autowired private com.api_3.api_3.service.notification.NotificationQueryService notificationQueryService;
    @Autowired private com.api_3.api_3.service.notification.NotificationManagementService notificationManagementService;

    public void notifyTaskCreated(Task task) { taskNotificationService.notifyTaskCreated(task); }
    public void notifyTaskUpdatedBroadcast(Task task) { taskNotificationService.notifyTaskUpdatedBroadcast(task); }
    public void notifyTaskUpdatedScoped(Task task) { taskNotificationService.notifyTaskUpdatedScoped(task); }
    public void notifyTaskDeletedScoped(Task task) { taskNotificationService.notifyTaskDeletedScoped(task); }

    public void notifyTaskAssigned(Task task, String newResponsibleUuid) { taskNotificationService.notifyTaskAssigned(task, newResponsibleUuid); }

    public void notifyTaskUnassigned(Task task, String oldResponsibleUuid) { taskNotificationService.notifyTaskUnassigned(task, oldResponsibleUuid); }

    public void notifyTaskComment(Task task, String authorUuid) { taskNotificationService.notifyTaskComment(task, authorUuid); }

    public void notifyTaskDueSoon(Task task) { taskNotificationService.notifyTaskDueSoon(task); }

    public List<Notification> listForCurrentUser(boolean unreadOnly) {
        return notificationQueryService.listForCurrentUser(unreadOnly);
    }

    public Optional<Notification> markRead(String id) { return notificationManagementService.markRead(id); }

    public void delete(String id) { notificationManagementService.delete(id); }

    public void notifyProjectMemberJoined(String projectUuid, String joinedUserUuid) {
        com.api_3.api_3.service.notification.ProjectNotificationService projectService = this.projectNotificationService;
        if (projectService != null) projectService.notifyMemberJoined(projectUuid, joinedUserUuid);
    }

    public void notifyProjectMemberLeft(String projectUuid, String leftUserUuid) {
        com.api_3.api_3.service.notification.ProjectNotificationService projectService = this.projectNotificationService;
        if (projectService != null) projectService.notifyMemberLeft(projectUuid, leftUserUuid);
    }
    public void notifyTeamMemberJoined(String teamUuid, String joinedUserUuid) {
        com.api_3.api_3.service.notification.TeamNotificationService teamService = this.teamNotificationService;
        if (teamService != null) teamService.notifyMemberJoined(teamUuid, joinedUserUuid);
    }

    public void notifyTeamMemberLeft(String teamUuid, String leftUserUuid) {
        com.api_3.api_3.service.notification.TeamNotificationService teamService = this.teamNotificationService;
        if (teamService != null) teamService.notifyMemberLeft(teamUuid, leftUserUuid);
    }
}
