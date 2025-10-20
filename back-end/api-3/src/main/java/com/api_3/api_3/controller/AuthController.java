package com.api_3.api_3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.dto.request.AuthRequest;
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public AuthResponse authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody User newUser) {
        AuthResponse response = authService.register(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}