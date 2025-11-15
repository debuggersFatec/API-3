package com.api_3.auth_service.model.entity;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.TypeAlias;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TypeAlias("com.api_3.api_3.model.entity.TaskComment")
public class TaskComment {

    private String uuid;
    private String comment;
    private Date createdAt;
    private User.UserRef user;

    public TaskComment(String comment, Date createdAt, User.UserRef user) {
        this.uuid = UUID.randomUUID().toString();
        this.comment = comment;
        this.createdAt = createdAt;
        this.user = user;
    }
}
