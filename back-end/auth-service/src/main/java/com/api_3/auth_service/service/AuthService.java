package com.api_3.auth_service.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api_3.auth_service.dto.request.AuthRequest;
import com.api_3.auth_service.dto.response.AuthResponse;
import com.api_3.auth_service.exception.EmailAlreadyExistsException;
import com.api_3.auth_service.exception.InvalidCredentialsException;
import com.api_3.auth_service.exception.UserNotFoundException;
import com.api_3.auth_service.mapper.UserMapper;
import com.api_3.auth_service.model.entity.Notification;
import com.api_3.auth_service.model.entity.Task;
import com.api_3.auth_service.model.entity.Teams;
import com.api_3.auth_service.model.entity.User;
import com.api_3.auth_service.repository.NotificationRepository;
import com.api_3.auth_service.repository.TaskRepository;
import com.api_3.auth_service.repository.TeamsRepository;
import com.api_3.auth_service.repository.UserRepository;
import com.api_3.auth_service.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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

        // JWT
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(principal);

        User user = userRepository.findByEmailIgnoreCase(authRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        List<Teams> teams = teamsRepository.findByMembersUuid(user.getUuid());
        if ((teams == null || teams.isEmpty()) && user.getTeams() != null) {
            teams = user.getTeams().stream()
                .map(ref -> teamsRepository.findById(ref.getUuid()).orElseGet(() -> {
                    Teams fallback = new Teams();
                    fallback.setUuid(ref.getUuid());
                    fallback.setName(ref.getName());
                    return fallback;
                }))
                .collect(Collectors.toList());
        }
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());
        AuthResponse.UserInfo userInfo = userMapper.toUserInfo(user, teams, tasks);
        AuthResponse.Routes routes = new AuthResponse.Routes(
            "/api/teams",
            "/api/projects",
            "/api/teams/{teamUuid}/members",
            "/api/tasks"
        );

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setRoutes(routes);
        resp.setUser(userInfo);
        resp.setNotificationsUnread(notificationRepository.countByUserUuidAndReadFalse(user.getUuid()));
        resp.setNotificationsRecent(
            notificationRepository.findTop20ByUserUuidOrderByCreatedAtDesc(user.getUuid()).stream()
                .map(this::toNotificationDto)
                .collect(Collectors.toList())
        );
        return resp;
    }

    public AuthResponse register(User newUser) {
        if (newUser == null || newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new IllegalArgumentException("Dados do usuário inválidos para registro");
        }

        newUser.setEmail(newUser.getEmail().trim());

        if (userRepository.existsByEmailIgnoreCase(newUser.getEmail())) {
            throw new EmailAlreadyExistsException("Este e-mail já está em uso.");
        }
        if (newUser.getUuid() == null || newUser.getUuid().isBlank()) {
            newUser.setUuid(UUID.randomUUID().toString());
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        if (newUser.getEquipeIds() == null) {
            newUser.setEquipeIds(Collections.emptyList());
        }
        if (newUser.getTasks() == null) {
            newUser.setTasks(new java.util.ArrayList<>());
        }

        User savedUser = userRepository.save(newUser);

        // JWT generation
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

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setRoutes(routes);
        resp.setUser(userInfo);
        resp.setNotificationsUnread(0L);
        resp.setNotificationsRecent(Collections.emptyList());
        return resp;
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para o e-mail: " + email));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private com.api_3.auth_service.dto.response.NotificationDto toNotificationDto(Notification notification) {
        return new com.api_3.auth_service.dto.response.NotificationDto(
            notification.getId(),
            notification.getType() != null ? notification.getType().name() : null,
            notification.getTeamUuid(),
            notification.getProjectUuid(),
            notification.getTaskUuid(),
            notification.getTaskTitle(),
            notification.getMessage(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
