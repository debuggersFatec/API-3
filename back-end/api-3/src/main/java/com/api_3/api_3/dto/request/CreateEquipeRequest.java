package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank; // Importe esta anotação
import lombok.Data;

@Data
public class CreateEquipeRequest {
    
    @NotBlank(message = "O nome da equipa é obrigatório.") // Adicione a anotação aqui
    private String name;
}