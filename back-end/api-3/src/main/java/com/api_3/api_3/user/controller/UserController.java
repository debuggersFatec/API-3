package com.api_3.api_3.user.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    
    // Endpoint de diagnóstico para verificar associações de usuários e equipes
    @GetMapping("/debug/user-teams")
    public ResponseEntity<?> debugUserTeams() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (User user : users) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("uuid", user.getUuid());
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("equipeIds", user.getEquipeIds());
            result.add(userInfo);
        }
        
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{userId}/equipes")
    public ResponseEntity<User>addEquipeToUser(@PathVariable String userId,@RequestBody Map<String, String> payload){
        String equipeId = payload.get("equipeId");
        if (equipeId == null){
            return ResponseEntity.badRequest().build();

        }
        return userRepository.findById(userId).map(user-> {
            if (!user.getEquipeIds().contains(equipeId)){
                user.getEquipeIds().add(equipeId);
                userRepository.save(user);
            }
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

}