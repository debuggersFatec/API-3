package com.api_3.api_3.service.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Notification.Type;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.CurrentUserService;

@Service
public class TeamNotificationService {

    @Autowired private TeamsRepository teamsRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CurrentUserService currentUserService;
    @Autowired private NotificationRecipientResolver recipientResolver;
    @Autowired private NotificationFactory notificationFactory;

    public void notifyMemberJoined(String teamUuid, String joinedUserUuid) {
        Teams team = teamsRepository.findById(teamUuid).orElse(null);
        if (team == null) return;
        String userName = userRepository.findById(joinedUserUuid).map(User::getName).orElse("Um usuário");
        java.util.Set<String> exclude = new java.util.HashSet<>();
        exclude.add(joinedUserUuid);
        notifyTeamMembers(team, Type.TEAM_MEMBER_JOINED, userName + " entrou na equipe " + team.getName(), false, exclude);
    }

    public void notifyMemberLeft(String teamUuid, String leftUserUuid) {
        Teams team = teamsRepository.findById(teamUuid).orElse(null);
        if (team == null) return;
        String userName = userRepository.findById(leftUserUuid).map(User::getName).orElse("Um usuário");
        java.util.Set<String> exclude = new java.util.HashSet<>();
        exclude.add(leftUserUuid);
        notifyTeamMembers(team, Type.TEAM_MEMBER_LEFT, userName + " saiu da equipe " + team.getName(), false, exclude);
    }

    // helper: broadcast to team members
    private void notifyTeamMembers(Teams team, Type type, String message, boolean includeActor, java.util.Set<String> exclude) {
        if (team == null || team.getMembers() == null || team.getMembers().isEmpty()) return;
        String actorUuid = currentUserService.currentUserUuid();
        java.util.Set<String> recipients = recipientResolver.teamRecipients(team, actorUuid, includeActor, exclude);
        if (recipients.isEmpty()) return;
        List<Notification> toSave = new ArrayList<>();
        for (String dest : recipients) {
            Notification n = notificationFactory.fromTeam(team, type, message, actorUuid, dest);
            toSave.add(n);
        }
        if (!toSave.isEmpty()) notificationRepository.saveAll(toSave);
    }
}
