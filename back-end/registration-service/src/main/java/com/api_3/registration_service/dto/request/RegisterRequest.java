package com.api_3.registration_service.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}