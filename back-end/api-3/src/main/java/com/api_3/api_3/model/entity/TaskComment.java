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
    private String authorUuid;

    public TaskComment(String comment, Date createdAt, String authorUuid) {
        this.uuid = UUID.randomUUID().toString();
        this.comment = comment;
        this.createdAt = createdAt;
        this.authorUuid = authorUuid; 
    }
}