package com.api_3.registration_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_3.registration_service.dto.request.RegisterRequest;
import com.api_3.registration_service.model.entity.User;
import com.api_3.registration_service.repository.UserRepository;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest request) {
        // Valida se o email já existe no banco
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este email já está em uso.");
        }

        // Criptografa a senha 
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
            request.getName(), 
            request.getEmail(), 
            encryptedPassword, 
            null
        );
        userRepository.save(newUser);
    }
}