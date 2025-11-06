package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// usado para receber o JSON: 
public record PasswordResetRequest(
    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    String novaSenha
) {}