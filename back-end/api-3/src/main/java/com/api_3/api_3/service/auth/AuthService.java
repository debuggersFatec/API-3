package com.api_3.api_3.service.auth;

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
import com.api_3.api_3.dto.response.NotificationDto;
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
// import com.api_3.api_3.service.notification.NotificationQueryService;

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
    // Notifications service is not available in this branch; default values will be returned in the response for now.
    // private NotificationQueryService notificationQueryService;

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

        List<Teams> teams = teamsRepository.findAllById(user.getEquipeIds());
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());
        if (tasks != null && !tasks.isEmpty()) {
            log.debug("AuthService.login: tasks from responsibleUuid -> {}", tasks.size());
        }

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
        resp.setNotificationsUnread(0L);
        resp.setNotificationsRecent(Collections.<NotificationDto>emptyList());

        return resp;
    }

    public AuthResponse googleLogin(String googleAccessToken, String email, String name, String picture) {

        if (email == null || googleAccessToken == null) {
            throw new IllegalArgumentException("Token de acesso ou e-mail inválidos");
        }

        Optional<User> existingUserOpt = userRepository.findByEmailIgnoreCase(email);
        User user;

        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();
            user.setGoogleAccessToken(googleAccessToken);
            userRepository.save(user);

        } else {
            user = new User();
            user.setUuid(UUID.randomUUID().toString());
            user.setName(name != null ? name : email.split("@")[0]);
            user.setEmail(email);
            user.setImg(picture);
            user.setGoogleAccessToken(googleAccessToken);
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            user.setEquipeIds(Collections.emptyList());
            user.setTasks(new java.util.ArrayList<>());

            userRepository.save(user);
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();

        String jwtToken = jwtUtil.generateToken(userDetails);

        List<Teams> teams = teamsRepository.findAllById(user.getEquipeIds());
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());

        AuthResponse.UserInfo userInfo = userMapper.toUserInfo(user, teams, tasks);

        AuthResponse.Routes routes = new AuthResponse.Routes(
            "/api/teams",
            "/api/projects",
            "/api/teams/{teamUuid}/members",
            "/api/tasks"
        );

        log.info("Usuário autenticado via Google: {}", user.getEmail());

        AuthResponse resp = new AuthResponse();
        resp.setToken(jwtToken);
        resp.setRoutes(routes);
        resp.setUser(userInfo);
        // Notifications are not wired yet in this branch; return sensible defaults
        resp.setNotificationsUnread(0L);
        resp.setNotificationsRecent(Collections.<NotificationDto>emptyList());
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

        List<Teams> teams = teamsRepository.findAllById(savedUser.getEquipeIds());
        AuthResponse.UserInfo userInfo = userMapper.toUserInfo(savedUser, teams, Collections.emptyList());
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
        // Notifications are not wired yet in this branch; return sensible defaults
        resp.setNotificationsUnread(0L);
        resp.setNotificationsRecent(Collections.<NotificationDto>emptyList());
        return resp;
    }

    public void updatePassword(String email, String newPassword) {
        // Usamos findByEmailIgnoreCase para consistência com o resto do service
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado para o e-mail: " + email));
        // Criptografa a nova senha
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
}