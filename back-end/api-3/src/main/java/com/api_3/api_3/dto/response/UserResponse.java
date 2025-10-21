package com.api_3.api_3.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class UserResponse {
    private String uuid;
    private String name;
    private String email;
    private String img;
    private List<String> equipeIds; // Incluído para o endpoint de debug
}