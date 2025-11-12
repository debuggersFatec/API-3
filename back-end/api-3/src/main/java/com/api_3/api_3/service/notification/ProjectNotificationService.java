package com.api_3.api_3.service.notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Notification.Type;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.CurrentUserService;

@Service
public class ProjectNotificationService {

    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CurrentUserService currentUserService;
    @Autowired private NotificationRecipientResolver recipientResolver;
    @Autowired private NotificationFactory notificationFactory;

    public void notifyMemberJoined(String projectUuid, String joinedUserUuid) {
        Projects project = projectsRepository.findById(projectUuid).orElse(null);
        if (project == null) return;
        String userName = userRepository.findById(joinedUserUuid).map(User::getName).orElse("Um usuário");
        java.util.Set<String> exclude = new java.util.HashSet<>();
        exclude.add(joinedUserUuid);
        notifyProjectMembers(project, Type.PROJECT_MEMBER_JOINED, userName + " entrou no projeto " + project.getName(), false, exclude);

        // Fallback: if no other recipients, notify actor so the event is persisted
        String actorUuid = currentUserService.currentUserUuid();
        boolean hasOtherRecipients = project.getMembers() != null && project.getMembers().stream()
            .anyMatch(m -> m != null && m.getUuid() != null && !m.getUuid().equals(actorUuid) && !m.getUuid().equals(joinedUserUuid));
        if (!hasOtherRecipients && actorUuid != null) {
            Notification n = new Notification();
            n.setType(Type.PROJECT_MEMBER_JOINED);
            n.setProjectUuid(project.getUuid());
            n.setTeamUuid(project.getTeamUuid());
            n.setMessage(userName + " entrou no projeto " + project.getName());
            n.setCreatedAt(Date.from(Instant.now()));
            n.setUserUuid(actorUuid);
            n.setActorUuid(actorUuid);
            notificationRepository.save(n);
        }
    }

    public void notifyMemberLeft(String projectUuid, String leftUserUuid) {
        Projects project = projectsRepository.findById(projectUuid).orElse(null);
        if (project == null) return;
        String userName = userRepository.findById(leftUserUuid).map(User::getName).orElse("Um usuário");
        java.util.Set<String> exclude = new java.util.HashSet<>();
        exclude.add(leftUserUuid);
        notifyProjectMembers(project, Type.PROJECT_MEMBER_LEFT, userName + " saiu do projeto " + project.getName(), false, exclude);

        String actorUuid = currentUserService.currentUserUuid();
        boolean hasOtherRecipients = project.getMembers() != null && project.getMembers().stream()
            .anyMatch(m -> m != null && m.getUuid() != null && !m.getUuid().equals(actorUuid) && !m.getUuid().equals(leftUserUuid));
        if (!hasOtherRecipients && actorUuid != null) {
            Notification n = new Notification();
            n.setType(Type.PROJECT_MEMBER_LEFT);
            n.setProjectUuid(project.getUuid());
            n.setTeamUuid(project.getTeamUuid());
            n.setMessage(userName + " saiu do projeto " + project.getName());
            n.setCreatedAt(Date.from(Instant.now()));
            n.setUserUuid(actorUuid);
            n.setActorUuid(actorUuid);
            notificationRepository.save(n);
        }
    }

    // helper: broadcast to project members
    private void notifyProjectMembers(Projects project, Type type, String message, boolean includeActor, java.util.Set<String> exclude) {
        if (project == null || project.getMembers() == null || project.getMembers().isEmpty()) return;
        String actorUuid = currentUserService.currentUserUuid();
        java.util.Set<String> recipients = recipientResolver.projectRecipients(project, actorUuid, includeActor, exclude);
        if (recipients.isEmpty()) return;
        List<Notification> toSave = new ArrayList<>();
        for (String dest : recipients) {
            Notification n = notificationFactory.fromProject(project, type, message, actorUuid, dest);
            toSave.add(n);
        }
        if (!toSave.isEmpty()) notificationRepository.saveAll(toSave);
    }
}
