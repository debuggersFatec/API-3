package com.api_3.registration_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.registration_service.dto.request.RegisterRequest;
import com.api_3.registration_service.service.RegistrationService;

@RestController
@RequestMapping("/auth") 
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            registrationService.registerUser(request);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (RuntimeException e) {
            // Se der erro (ex: email já existe), devolve erro 400
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}