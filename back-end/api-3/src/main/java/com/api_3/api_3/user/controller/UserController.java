package com.api_3.api_3.user.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.api_3.api_3.auth.AuthResponse;
import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.task.model.Task;
import com.api_3.api_3.task.repository.TaskRepository;
import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;

@RestController
@RequestMapping("/api/users") 
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipeRepository equipeRepository; // buscar equipas

    @Autowired
    private TaskRepository taskRepository; //  buscar tarefas

    // Rota para buscar os dados do utilizador autenticado
    @GetMapping("/me")
    public ResponseEntity<AuthResponse.UserInfo> getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        // Busca as equipas e tarefas associadas ao utilizador
        List<Equipe> equipesCompletas = equipeRepository.findAllById(user.getEquipeIds());
        List<AuthResponse.EquipeInfo> equipes = equipesCompletas.stream()
                .map(equipe -> new AuthResponse.EquipeInfo(equipe.getUuid(), equipe.getName()))
                .collect(Collectors.toList());
        
        List<Task> tasksDoUsuario = taskRepository.findByResponsibleUuid(user.getUuid());
        List<AuthResponse.TaskInfo> tasks = tasksDoUsuario.stream()
                .map(task -> new AuthResponse.TaskInfo(
                        task.getUuid(),
                        task.getTitle(),
                        task.getStatus(),
                        task.getPriority(),
                        task.getEquip_uuid()
                ))
                .collect(Collectors.toList());
        
        // Monta o objeto de resposta com todas as informações
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getImg(),
                equipes,
                tasks
        );
        
        return ResponseEntity.ok(userInfo);
    }

    // Rota para adicionar uma equipa a um utilizador
    @PutMapping("/{userId}/equipes")
    public ResponseEntity<User> addEquipeToUser(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        String equipeId = payload.get("equipeId");
        if (equipeId == null) {
            return ResponseEntity.badRequest().build();
        }
        return userRepository.findById(userId).map(user -> {
            if (!user.getEquipeIds().contains(equipeId)) {
                user.getEquipeIds().add(equipeId);
                userRepository.save(user);
            }
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }
}