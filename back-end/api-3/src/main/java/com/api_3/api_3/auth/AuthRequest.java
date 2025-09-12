package com.api_3.api_3.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}