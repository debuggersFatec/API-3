package com.api_3.api_3.auth;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.exception.EmailAlreadyExistsException;
import com.api_3.api_3.exception.InvalidCredentialsException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.security.JwtUtil;
import com.api_3.api_3.task.model.Task;
import com.api_3.api_3.task.repository.TaskRepository;
import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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

    @PostMapping("/login")
    public AuthResponse authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
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
        
        List<Equipe> equipesCompletas = equipeRepository.findAllById(user.getEquipeIds());
        List<AuthResponse.EquipeInfo> equipes = equipesCompletas.stream()
                .map(equipe -> new AuthResponse.EquipeInfo(equipe.getUuid(), equipe.getName()))
                .collect(Collectors.toList());
        
        List<Task> tasksDoUsuario = taskRepository.findByResponsibleUuid(user.getUuid());
        List<AuthResponse.TaskInfo> tasks = tasksDoUsuario.stream()
                .map(task -> new AuthResponse.TaskInfo(
                        task.getUuid(),
                        task.getTitle(),
                        task.getStatus(),
                        task.getPriority(),
                        task.getEquip_uuid(),
                        task.getDue_date()
                        
                ))
                .collect(Collectors.toList());
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getImg(),
                equipes,
                tasks
        );
        
        return new AuthResponse(token, userInfo);
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Erro: E-mail já está em uso!");
        }
        
        newUser.setUuid(UUID.randomUUID().toString());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        
        if (newUser.getEquipeIds() == null) {
            newUser.setEquipeIds(Collections.emptyList());
        }
        
        User savedUser = userRepository.save(newUser);
        
        String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(savedUser.getEmail())
                        .password(savedUser.getPassword())
                        .authorities("USER")
                        .build()
        );
        
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                savedUser.getUuid(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getImg(),
                Collections.emptyList(),
                Collections.emptyList()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, userInfo));
    }
}