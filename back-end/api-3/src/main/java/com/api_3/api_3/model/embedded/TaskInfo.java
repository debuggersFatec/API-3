package com.api_3.api_3.model.embedded;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskInfo {
    private String uuid;
    private String title;
    private String due_date;
    private String status;
    private String prioridade;
    private String equipe_uuid;
}