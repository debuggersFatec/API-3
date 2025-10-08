package com.api_3.api_3.controller;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.dto.response.UserResponse;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.service.GetUserService;
import com.api_3.api_3.service.UpdateUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.api_3.api_3.dto.request.AddEquipeToUserRequest;
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

    @GetMapping("/debug/user-teams")
    public ResponseEntity<List<UserResponse>> debugUserTeams() {
        List<User> users = getUserService.findAllUsersForDebug();
        return ResponseEntity.ok(userMapper.toUserResponseList(users));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserInfo> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        AuthResponse.UserInfo userInfo = getUserService.findCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(userInfo);
    }

    @PutMapping("/{userId}/equipes")
public ResponseEntity<UserResponse> addEquipeToUser(
        @PathVariable String userId,
        @Valid @RequestBody AddEquipeToUserRequest request) {
    User updatedUser = updateUserService.addEquipeToUser(userId, request.getEquipeId());
    return ResponseEntity.ok(userMapper.toUserResponse(updatedUser));
}
}