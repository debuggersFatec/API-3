package com.api_3.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Este record será usado para receber o JSON: { "email": "usuario@email.com" }
public record PasswordRequestEmailDto(
    @NotBlank(message = "O e-mail é obrigatório.") 
    @Email(message = "O e-mail deve ser válido.")
    String email
) {}