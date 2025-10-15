package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateEquipeRequest {
    
    @NotBlank(message = "O nome da equipe é obrigatório.")
    private String name;
}