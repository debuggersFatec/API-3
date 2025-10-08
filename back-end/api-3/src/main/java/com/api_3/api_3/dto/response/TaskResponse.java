package com.api_3.api_3.dto.response;

import com.api_3.api_3.model.entity.Responsible;
import lombok.Data;
import java.util.Date;

@Data
public class TaskResponse {
    private String uuid;
    private String title;
    private String description;
    private Date due_date;
    private String status;
    private String priority;
    private String equip_uuid;
    private Responsible responsible;
}