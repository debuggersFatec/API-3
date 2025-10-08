package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddEquipeToUserRequest {

    @NotBlank(message = "O campo 'equipeId' é obrigatório.")
    private String equipeId;
}