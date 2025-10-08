package com.api_3.api_3.dto.request;

import com.api_3.api_3.model.entity.Responsible;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Date;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "O título da tarefa é obrigatório.")
    private String title;

    private String description;

    private Date due_date;

    private String status;

    private String priority;

    @NotBlank(message = "O ID da equipe é obrigatório.")
    private String equip_uuid;

    private Responsible responsible; // Pode ser nulo se não houver responsável atribuído
}