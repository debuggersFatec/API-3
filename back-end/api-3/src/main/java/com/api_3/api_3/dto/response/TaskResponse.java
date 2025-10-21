package com.api_3.api_3.dto.response;

import java.util.Date;

import com.api_3.api_3.model.entity.Responsible;

import lombok.Data;

@Data
public class TaskResponse {
    private String uuid;
    private String title;
    private String description;
    private Date due_date;
    private String status;
    private String priority;
    private Boolean isRequiredFile;
    private String requiredFile;
    private String team_uuid;
    @Deprecated
    private String equip_uuid;
    private String project_uuid;
    private Responsible responsible;
}