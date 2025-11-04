package com.api_3.api_3.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String id;
    private String type;
    private String teamUuid;
    private String projectUuid;
    private String taskUuid;
    private String taskTitle;
    private String message;
    private boolean read;
    private Date createdAt;
}
