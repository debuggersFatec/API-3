package com.api_3.api_3.service;

import com.api_3.api_3.dto.request.AuthRequest;
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.exception.EmailAlreadyExistsException;
import com.api_3.api_3.exception.InvalidCredentialsException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipeRepository equipeRepository;
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserMapper userMapper;

    public AuthResponse login(AuthRequest authRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Credenciais inválidas!");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<Equipe> equipes = equipeRepository.findAllById(user.getEquipeIds());
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());

    AuthResponse.UserInfo userInfo = userMapper.toUserInfo(user, equipes, tasks);
    AuthResponse.Routes routes = new AuthResponse.Routes(
        "/api/teams",
        "/api/projects",
        "/api/teams/{teamUuid}/members",
        "/api/tasks"
    );
        
    return new AuthResponse(token, routes, userInfo);
    }

    public AuthResponse register(User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Erro: E-mail já está em uso!");
        }
        
        newUser.setUuid(UUID.randomUUID().toString());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        
        if (newUser.getEquipeIds() == null) {
            newUser.setEquipeIds(Collections.emptyList());
        }
        
        User savedUser = userRepository.save(newUser);
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(savedUser.getEmail())
            .password(savedUser.getPassword()) 
            .authorities("USER") 
            .build();

        String token = jwtUtil.generateToken(userDetails);
        
    AuthResponse.UserInfo userInfo = userMapper.toUserInfo(savedUser, Collections.emptyList(), Collections.emptyList());
    AuthResponse.Routes routes = new AuthResponse.Routes(
        "/api/teams",
        "/api/projects",
        "/api/teams/{teamUuid}/members",
        "/api/tasks"
    );
        
    return new AuthResponse(token, routes, userInfo);
    }
}