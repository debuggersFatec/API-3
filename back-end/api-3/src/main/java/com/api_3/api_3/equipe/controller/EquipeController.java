package com.api_3.api_3.equipe.controller;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.UUID;

import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;
import com.api_3.api_3.equipe.repository.EquipeRepository;
 
@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipeRepository equipeRepository;


    // CREATE -> Criar uma nova equipe
    @PostMapping
    public ResponseEntity<Equipe> createEquipe(@RequestBody Equipe novaEquipe) {
        novaEquipe.setUuid(UUID.randomUUID().toString());
        Equipe equipeSalva = equipeRepository.save(novaEquipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(equipeSalva);
    }

    // READ -> Obter as equipes do usuario
    @GetMapping
    public ResponseEntity<List<Equipe>> getEquipesDoUsuario(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        // Busca o usuário para obter os IDs das equipes
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        } 
        // 2. Obtém a lista de IDs das equipes
        List<String> equipeIds = userOptional.get().getEquipeIds();   
        // Busca todas as equipes com base nos IDs
        List<Equipe> equipes = equipeRepository.findAllById(equipeIds);

        return ResponseEntity.ok(equipes);
    }

    // READ -> Obter uma equipe específica por UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<Equipe> getEquipeById(@PathVariable String uuid) {
        return equipeRepository.findById(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  UPDATE -> Atualiza uma equipe existente (tasks,membros)
    @PutMapping("/{uuid}")
    public ResponseEntity<Equipe> updateEquipe(@PathVariable String uuid, @RequestBody Equipe equipeAtualizada) {
        return equipeRepository.findById(uuid).map(equipeExistente -> {
            equipeExistente.setName(equipeAtualizada.getName());
            equipeExistente.setMembros(equipeAtualizada.getMembros());
            equipeExistente.setTasks(equipeAtualizada.getTasks());
            Equipe savedEquipe = equipeRepository.save(equipeExistente);
            return ResponseEntity.ok(savedEquipe);
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE -> Deletar uma equipe existente
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEquipe(@PathVariable String uuid) {
        if (!equipeRepository.existsById(uuid)) {
            return ResponseEntity.notFound().build();
        }
        equipeRepository.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }


}