package com.api_3.api_3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api_3.api_3.dto.request.UpdateUserRequest;
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.dto.response.NotificationDto;
import com.api_3.api_3.dto.response.UserResponse;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.NotificationRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.JwtUtil;
import com.api_3.api_3.service.notification.NotificationQueryService;
import com.api_3.api_3.service.user.GetUserService;
import com.api_3.api_3.service.user.UpdateUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operações de usuário")
public class UserController {

    @Autowired
    private GetUserService getUserService;
    @Autowired
    private UpdateUserService updateUserService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationQueryService notificationQueryService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/debug/user-teams")
    public ResponseEntity<List<UserResponse>> debugUserTeams() {
        List<User> users = getUserService.findAllUsersForDebug();
        return ResponseEntity.ok(userMapper.toUserResponseList(users));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter usuário autenticado", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AuthResponse.UserInfo userInfo = getUserService.findCurrentUserProfile(userDetails.getUsername());
        AuthResponse.Routes routes = new AuthResponse.Routes(
            "/api/teams",
            "/api/projects",
            "/api/teams/{teamUuid}/members",
            "/api/tasks"
        );
        // Gerar token também para /me a pedido do cliente
        String token = jwtUtil.generateToken(userDetails);

        // Preencher notificações
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setRoutes(routes);
        resp.setUser(userInfo);
        com.api_3.api_3.model.entity.User u = userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElse(null);
        if (u != null) {
            long unread = notificationQueryService.countUnreadByUserUuid(u.getUuid());
            java.util.List<NotificationDto> recent = notificationQueryService.findRecentTop20DtosByUserUuid(u.getUuid());
            resp.setNotificationsUnread(unread);
            resp.setNotificationsRecent(recent);
        }
        return ResponseEntity.ok(resp);
    }

    // Atualiza o usuário autenticado (nome e imagem via string/link)
    @PutMapping("/me")
    @Operation(summary = "Atualizar nome e imagem do usuário autenticado", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        User updated = updateUserService.updateCurrentUser(request);
        return ResponseEntity.ok(userMapper.toUserResponse(updated));
    }

    // NOVA ROTA: Upload de imagem de perfil
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de nova foto de perfil", description = "Envia uma imagem (jpg, png, webp) de até 50MB para atualizar o perfil.", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<UserResponse> updateProfileImage(@RequestParam("file") MultipartFile file) {
        User updated = updateUserService.updateProfilePicture(file);
        return ResponseEntity.ok(userMapper.toUserResponse(updated));
    }
}