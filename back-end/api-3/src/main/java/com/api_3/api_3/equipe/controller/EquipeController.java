package com.api_3.api_3.equipe.controller;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.user.repository.UserRepository;
 
@RestController
@RequestMapping("/api/equipes")
public class EquipeController {
    @Autowired
    private UserRepository userRepository;
    // READ -> Obter as equipes do usuario
    @GetMapping
    public ResponseEntity<List<Equipe>> getEquipesDoUsuario(Authentication authentication) {
        // Validação
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();
        return userRepository.findByEmail(userEmail)
                .map(user -> ResponseEntity.ok(user.getEquipes()))
                .orElse(ResponseEntity.notFound().build());
    }}