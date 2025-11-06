package com.api_3.api_3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.dto.request.UpdateUserRequest;
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.dto.response.UserResponse;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.security.JwtUtil;
import com.api_3.api_3.service.AuthResponseBuilder;
import com.api_3.api_3.service.GetUserService;
import com.api_3.api_3.service.UpdateUserService;

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
    private AuthResponseBuilder authResponseBuilder;

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

        String token = jwtUtil.generateToken(userDetails);
        
        return ResponseEntity.ok(authResponseBuilder.build(token, userInfo, userInfo.getUuid()));
    }

    // Atualiza o usuário autenticado (nome e imagem)
    @PutMapping("/me")
    @Operation(summary = "Atualizar nome e imagem do usuário autenticado", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        User updated = updateUserService.updateCurrentUser(request);
        return ResponseEntity.ok(userMapper.toUserResponse(updated));
    }
}