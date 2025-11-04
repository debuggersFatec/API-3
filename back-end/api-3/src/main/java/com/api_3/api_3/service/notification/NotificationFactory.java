package com.api_3.api_3.service.notification;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.api_3.api_3.model.entity.Notification;
import com.api_3.api_3.model.entity.Notification.Type;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;

@Component
public class NotificationFactory {

    public Notification fromTask(Task task, Type type, String message, String actorUuid, String userUuid) {
        Notification n = new Notification();
        n.setType(type);
        n.setTaskUuid(task.getUuid());
        n.setTaskTitle(task.getTitle());
        n.setProjectUuid(task.getProjectUuid());
        n.setTeamUuid(task.getEquip_uuid());
        n.setMessage(message);
        n.setCreatedAt(new Date());
        n.setActorUuid(actorUuid);
        n.setUserUuid(userUuid);
        return n;
    }

    public Notification fromProject(Projects project, Type type, String message, String actorUuid, String userUuid) {
        Notification n = new Notification();
        n.setType(type);
        n.setProjectUuid(project.getUuid());
        n.setTeamUuid(project.getTeamUuid());
        n.setMessage(message);
        n.setCreatedAt(new Date());
        n.setActorUuid(actorUuid);
        n.setUserUuid(userUuid);
        return n;
    }

    public Notification fromTeam(Teams team, Type type, String message, String actorUuid, String userUuid) {
        Notification n = new Notification();
        n.setType(type);
        n.setTeamUuid(team.getUuid());
        n.setMessage(message);
        n.setCreatedAt(new Date());
        n.setActorUuid(actorUuid);
        n.setUserUuid(userUuid);
        return n;
    }
}
