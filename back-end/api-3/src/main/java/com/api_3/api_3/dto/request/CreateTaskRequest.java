package com.api_3.api_3.dto.request;

import java.util.Date;
import com.api_3.api_3.model.entity.Responsible;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "O título da tarefa é obrigatório.")
    private String title;

    private String description;
    private Date due_date;
    private String status;
    private String priority;

    @NotBlank(message = "O ID do projeto é obrigatório.")
    private String project_uuid;

    private String equip_uuid; 
    private Responsible responsible;


    private Boolean isRequiredFile; 
    private String requiredFile;
}