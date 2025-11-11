package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
    @NotBlank @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres") String newPassword
) {}
package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// usado para receber o JSON: 
public record PasswordResetRequest(
    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial.")
    String newPassword
) {}