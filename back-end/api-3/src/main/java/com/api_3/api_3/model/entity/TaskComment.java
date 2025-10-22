package com.api_3.api_3.model.entity;

import java.util.Date;
import java.util.UUID; 

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskComment {

    private String uuid;
    private String comment;
    private Date createdAt;
    private User.UserRef user;

    public TaskComment(String comment, Date createdAt, User.UserRef user) {
        this.uuid = UUID.randomUUID().toString(); // Gerar UUID automaticamente
        this.comment = comment;
        this.createdAt = createdAt;
        this.user = user;
    }
}