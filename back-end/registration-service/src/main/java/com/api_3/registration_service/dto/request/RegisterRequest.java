package com.api_3.registration_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data; 

@Data
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    // Explicação da Regex:
    // (?=.*[0-9])       -> Pelo menos um número
    // (?=.*[a-z])       -> Pelo menos uma letra minúscula
    // (?=.*[A-Z])       -> Pelo menos uma letra maiúscula
    // (?=.*[@#$%^&+=!]) -> Pelo menos um caracter especial (adicione outros se precisar)
    // .{8,}             -> No mínimo 8 caracteres
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", 
        message = "A senha deve ter no mínimo 8 caracteres, conter letra maiúscula, minúscula, número e caractere especial (@#$%^&+=!)."
    )
    private String password;
}