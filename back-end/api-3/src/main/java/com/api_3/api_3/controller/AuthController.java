package com.api_3.api_3.controller;

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

import com.api_3.api_3.dto.request.AuthRequest;
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.exception.EmailAlreadyExistsException;
import com.api_3.api_3.exception.InvalidCredentialsException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.JwtUtil;

import jakarta.validation.Valid;

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
    private TeamsRepository teamsRepository;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectsRepository projectsRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
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
        
    // Teams listing for this user
    List<Teams> userTeams = teamsRepository.findAllById(user.getEquipeIds()); // compatibility shim
    List<AuthResponse.TeamInfo> teams = userTeams.stream()
        .map(team -> new AuthResponse.TeamInfo(team.getUuid(), team.getName()))
        .collect(Collectors.toList());
        
        
    // Build projects list for the user's teams
    List<AuthResponse.ProjectInfo> projects = userTeams.stream()
        .flatMap(t -> projectsRepository.findByTeamUuid(t.getUuid()).stream())
        .map(p -> new AuthResponse.ProjectInfo(p.getUuid(), p.getName(), p.isActive(), p.getTeamUuid()))
        .collect(Collectors.toList());

    AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getImg(),
        teams,
        projects,
        Collections.emptyList()
        );
    AuthResponse.Routes routes = new AuthResponse.Routes(
        "/api/teams",
        "/api/projects",
        "/api/teams/{teamUuid}/members",
        "/api/tasks"
    );
        
    return new AuthResponse(token, routes, userInfo);
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody User newUser) {
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
                Collections.emptyList(),
                Collections.emptyList()
        );
    AuthResponse.Routes routes = new AuthResponse.Routes(
        "/api/teams",
        "/api/projects",
        "/api/teams/{teamUuid}/members",
        "/api/tasks"
    );
        
    return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, routes, userInfo));
    }
}