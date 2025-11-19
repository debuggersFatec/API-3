package com.api_3.registration_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_3.registration_service.dto.request.RegisterRequest;
import com.api_3.registration_service.dto.response.AuthResponse;
import com.api_3.registration_service.dto.response.UserDto;
import com.api_3.registration_service.model.entity.User;
import com.api_3.registration_service.repository.UserRepository;
import com.api_3.registration_service.security.JwtUtil;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil; 

    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Este email já está em uso.");
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
            request.getName(), 
            request.getEmail(), 
            encryptedPassword, 
            null 
        );

        User savedUser = userRepository.save(newUser);

        String token = jwtUtil.generateToken(savedUser);

        UserDto userDto = new UserDto(
            savedUser.getUuid(), 
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getImg()
        );

        // Retorna o objeto completo que o front espera
        return new AuthResponse(token, userDto);
    }
}