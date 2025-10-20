package com.api_3.api_3.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.JwtUtil;

@Service
public class AuthService {

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

        // For compatibility, we still read user.getEquipeIds(), but resolve them as Teams
        List<Teams> teams = teamsRepository.findAllById(user.getEquipeIds());
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());
        if ((tasks == null || tasks.isEmpty()) && user.getTasks() != null && !user.getTasks().isEmpty()) {
            // Fallback: resolve tasks by IDs stored in the user's embedded task refs
            java.util.List<String> ids = user.getTasks().stream()
                    .map(t -> t.uuid())
                    .filter(java.util.Objects::nonNull)
                    .toList();
            if (!ids.isEmpty()) {
                tasks = taskRepository.findAllById(ids);
            }
        }
        // Final fallback: scan projects under user's teams for task refs assigned to user
        if (tasks == null || tasks.isEmpty()) {
            java.util.List<String> taskIds = teams.stream()
                .flatMap(t -> projectsRepository.findByTeamUuid(t.getUuid()).stream())
                .filter(p -> p.getTasks() != null)
                .flatMap(p -> p.getTasks().stream())
                .filter(tp -> tp != null && tp.getResponsible() != null && user.getUuid().equals(tp.getResponsible().uuid()))
                .map(tp -> tp.getUuid())
                .distinct()
                .toList();
            if (!taskIds.isEmpty()) {
                tasks = taskRepository.findAllById(taskIds);
            }
        }
        // Last resort: include all tasks from user's teams' projects (excluding DELETED)
        if (tasks == null || tasks.isEmpty()) {
            java.util.List<String> allTaskIds = teams.stream()
                .flatMap(t -> projectsRepository.findByTeamUuid(t.getUuid()).stream())
                .filter(p -> p.getTasks() != null)
                .flatMap(p -> p.getTasks().stream())
                .filter(tp -> tp != null && tp.getStatus() != null && tp.getStatus() != Task.Status.DELETED)
                .map(tp -> tp.getUuid())
                .distinct()
                .toList();
            if (!allTaskIds.isEmpty()) {
                tasks = taskRepository.findAllById(allTaskIds);
            }
        }

    AuthResponse.UserInfo userInfo = userMapper.toUserInfo(user, teams, tasks);
    AuthResponse.Routes routes = new AuthResponse.Routes(
        "/api/teams",
        "/api/projects",
        "/api/teams/{teamUuid}/members",
        "/api/tasks"
    );
        
    return new AuthResponse(token, routes, userInfo);
    }

    public AuthResponse register(User newUser) {
        if (newUser.getEmail() != null) newUser.setEmail(newUser.getEmail().trim());
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new EmailAlreadyExistsException("Erro: E-mail inválido!");
        }
        if (userRepository.existsByEmailIgnoreCase(newUser.getEmail())) {
            throw new EmailAlreadyExistsException("Erro: E-mail já está em uso!");
        }
        
        newUser.setUuid(UUID.randomUUID().toString());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        
        if (newUser.getEquipeIds() == null) {
            newUser.setEquipeIds(Collections.<String>emptyList());
        }
        
        User savedUser = userRepository.save(newUser);
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(savedUser.getEmail())
            .password(savedUser.getPassword()) 
            .authorities("USER") 
            .build();

        String token = jwtUtil.generateToken(userDetails);
        
    AuthResponse.UserInfo userInfo = userMapper.toUserInfo(savedUser, Collections.<Teams>emptyList(), Collections.emptyList());
    AuthResponse.Routes routes = new AuthResponse.Routes(
        "/api/teams",
        "/api/projects",
        "/api/teams/{teamUuid}/members",
        "/api/tasks"
    );
        
    return new AuthResponse(token, routes, userInfo);
    }
}