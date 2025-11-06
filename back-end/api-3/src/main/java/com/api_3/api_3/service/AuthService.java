package com.api_3.api_3.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_3.api_3.dto.request.AuthRequest;
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.exception.EmailAlreadyExistsException;
import com.api_3.api_3.exception.InvalidCredentialsException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.JwtUtil;

@Service
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private AuthResponseBuilder authResponseBuilder;

    public AuthResponse login(AuthRequest authRequest) {
        // Authenticate credentials
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Credenciais inválidas!");
        }

        // Generate JWT
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(principal);

        // Load user and related data
        User user = userRepository.findByEmailIgnoreCase(authRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<Teams> teams = teamsRepository.findAllById(user.getEquipeIds());
        // Assigned-only: only tasks where the user is responsible
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());
        if (tasks != null && !tasks.isEmpty()) {
            log.debug("AuthService.login: tasks from responsibleUuid -> {}", tasks.size());
        }

        AuthResponse.UserInfo userInfo = userMapper.toUserInfo(user, teams, tasks);
        return authResponseBuilder.build(token, userInfo, user.getUuid());
    }

    public AuthResponse register(User newUser) {
        if (newUser == null || newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new IllegalArgumentException("Dados do usuário inválidos para registro");
        }

        // Normalize email (optional but recommended)
        newUser.setEmail(newUser.getEmail().trim());

        if (userRepository.existsByEmailIgnoreCase(newUser.getEmail())) {
            throw new EmailAlreadyExistsException("Este e-mail já está em uso.");
        }

        // Ensure UUID and encode password
        if (newUser.getUuid() == null || newUser.getUuid().isBlank()) {
            newUser.setUuid(UUID.randomUUID().toString());
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // Ensure legacy compatibility fields are initialized
        if (newUser.getEquipeIds() == null) {
            newUser.setEquipeIds(Collections.emptyList());
        }
        if (newUser.getTasks() == null) {
            newUser.setTasks(new java.util.ArrayList<>());
        }

        User savedUser = userRepository.save(newUser);

        // Build JWT for the new user
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(savedUser.getEmail())
            .password(savedUser.getPassword())
            .authorities("USER")
            .build();
        String token = jwtUtil.generateToken(userDetails);

        List<Teams> teams = teamsRepository.findAllById(savedUser.getEquipeIds());
        AuthResponse.UserInfo userInfo = userMapper.toUserInfo(savedUser, teams, Collections.emptyList());
        // Registro: ainda assim usamos o builder (vai retornar 0/unread vazio)
        return authResponseBuilder.build(token, userInfo, savedUser.getUuid());
    }
}