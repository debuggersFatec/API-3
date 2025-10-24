package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank(message = "O conteúdo do comentário não pode estar vazio.")
    private String content;
}