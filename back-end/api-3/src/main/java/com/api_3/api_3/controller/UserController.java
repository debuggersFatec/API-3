package com.api_3.api_3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.dto.request.AddEquipeToUserRequest;
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.dto.response.UserResponse;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.security.JwtUtil;
import com.api_3.api_3.service.GetUserService;
import com.api_3.api_3.service.UpdateUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private GetUserService getUserService;
    @Autowired
    private UpdateUserService updateUserService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/debug/user-teams")
    public ResponseEntity<List<UserResponse>> debugUserTeams() {
        List<User> users = getUserService.findAllUsersForDebug();
        return ResponseEntity.ok(userMapper.toUserResponseList(users));
    }

    @GetMapping("/me")
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
        return ResponseEntity.ok(new AuthResponse(token, routes, userInfo));
    }

    @PutMapping("/{userId}/equipes")
    public ResponseEntity<UserResponse> addEquipeToUser(
        @PathVariable String userId,
        @Valid @RequestBody AddEquipeToUserRequest request) {
    User updatedUser = updateUserService.addEquipeToUser(userId, request.getEquipeId());
    return ResponseEntity.ok(userMapper.toUserResponse(updatedUser));
}
}