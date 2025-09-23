package com.api_3.api_3.auth;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            
            // Buscar os dados completos do usuário
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Buscar equipes do usuário
            List<Equipe> equipesCompletas = equipeRepository.findAllById(user.getEquipeIds());
            List<AuthResponse.EquipeInfo> equipes = equipesCompletas.stream()
                    .map(equipe -> new AuthResponse.EquipeInfo(equipe.getUuid(), equipe.getName()))
                    .collect(Collectors.toList());
            
            // Buscar tarefas atribuídas ao usuário
            List<Task> tasksDoUsuario = taskRepository.findByResponsibleUuid(user.getUuid());
            List<AuthResponse.TaskInfo> tasks = tasksDoUsuario.stream()
                    .map(task -> new AuthResponse.TaskInfo(
                            task.getUuid(),
                            task.getTitle(),
                            task.getStatus(),
                            task.getPriority(),
                            task.getEquip_uuid()
                    ))
                    .collect(Collectors.toList());
            
            // Criar o objeto de resposta com token e dados do usuário
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getUuid(),
                    user.getName(),
                    user.getEmail(),
                    user.getImg(),
                    equipes,
                    tasks
            );
            
            return new AuthResponse(token, userInfo);
        } else {
            throw new RuntimeException("Invalid username or password!");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: E-mail já está em uso!");
        }
        newUser.setUuid(UUID.randomUUID().toString());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User savedUser = userRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}