package com.api_3.api_3.dto.request;

import java.util.Date;

import com.api_3.api_3.model.entity.Responsible;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTaskRequest {

    @NotBlank(message = "O título da tarefa é obrigatório.")
    private String title;

    private String description;

    private Date due_date;

    private String status;

    private String priority;

    private Responsible responsible; 

    private String requiredFile;

    private Boolean isRequiredFile;
}