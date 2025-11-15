package com.api_3.auth_service.model.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "notifications")
@TypeAlias("com.api_3.api_3.model.entity.Notification")
@Data
@NoArgsConstructor
public class Notification {
    @Id
    private String id;

    private String userUuid;
    private Type type;

    private String taskUuid;
    private String taskTitle;
    private String projectUuid;
    private String teamUuid;
    private String actorUuid;

    private String message;

    private Date createdAt = new Date();
    private boolean read = false;
    private Date readAt;

    public enum Type {
        TASK_CREATED,
        TASK_UPDATED,
        TASK_DELETED,
        TASK_DUE_SOON,
        TASK_ASSIGNED,
        TASK_UNASSIGNED,
        TASK_COMMENT,
        TEAM_MEMBER_JOINED,
        TEAM_MEMBER_LEFT,
        PROJECT_MEMBER_JOINED,
        PROJECT_MEMBER_LEFT
    }
}
