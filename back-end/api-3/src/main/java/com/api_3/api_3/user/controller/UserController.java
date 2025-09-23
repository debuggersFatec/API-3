package com.api_3.api_3.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // Método registerUser foi movido para o AuthController

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