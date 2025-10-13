package com.api_3.api_3.model.entity;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskComment {

    private String uuid;
    private String comment;
    private Date createdAt;
    private User.UserRef user;

    public TaskComment(String uuid, String comment, Date createdAt, User.UserRef user) {
        this.uuid = uuid;
        this.comment = comment;
        this.createdAt = createdAt;
        this.user = user;
    }
}
