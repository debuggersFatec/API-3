package com.api_3.api_3.controller;

import com.api_3.api_3.dto.request.CreateEquipeRequest;
import com.api_3.api_3.dto.response.EquipeResponse;
import com.api_3.api_3.mapper.EquipeMapper;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeController {
    @Autowired private CreateEquipeService createEquipeService;
    @Autowired private GetEquipesService getEquipesService;
    @Autowired private UpdateEquipeService updateEquipeService;
    @Autowired private DeleteEquipeService deleteEquipeService;
    @Autowired private AddMembroEquipeService addMembroEquipeService;
    @Autowired private RemoveMembroEquipeService removeMembroEquipeService;

    @Autowired
    private EquipeMapper equipeMapper;

    private UserDetails getUserDetails(Authentication authentication) {
        return (UserDetails) authentication.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<EquipeResponse> createEquipe(@Valid @RequestBody CreateEquipeRequest request, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        Equipe novaEquipe = createEquipeService.execute(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(equipeMapper.toEquipeResponse(novaEquipe));
    }

    @GetMapping
    public ResponseEntity<List<EquipeResponse>> getEquipesDoUsuario(Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        List<Equipe> equipes = getEquipesService.findAllForUser(userDetails.getUsername());
        return ResponseEntity.ok(equipeMapper.toEquipeResponseList(equipes));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EquipeResponse> getEquipeById(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            Equipe equipe = getEquipesService.findByIdAndVerifyMembership(uuid, userDetails.getUsername());
            return ResponseEntity.ok(equipeMapper.toEquipeResponse(equipe));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<EquipeResponse> updateEquipe(@PathVariable String uuid, @RequestBody Equipe equipeAtualizada, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            getEquipesService.findByIdAndVerifyMembership(uuid, userDetails.getUsername());
            Equipe equipe = updateEquipeService.execute(uuid, equipeAtualizada);
            return ResponseEntity.ok(equipeMapper.toEquipeResponse(equipe));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEquipe(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            getEquipesService.findByIdAndVerifyMembership(uuid, userDetails.getUsername());
            deleteEquipeService.execute(uuid);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // MÉTODOS DE GESTÃO DE MEMBROS

    @PostMapping("/{equipeId}/membros/{membroId}")
    public ResponseEntity<String> addMembroNaEquipe(@PathVariable String equipeId, @PathVariable String membroId, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            getEquipesService.findByIdAndVerifyMembership(equipeId, userDetails.getUsername());
            addMembroEquipeService.execute(equipeId, membroId);
            return ResponseEntity.ok("Membro adicionado com sucesso");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{equipeId}/membros/{membroId}")
    public ResponseEntity<String> removeMembroDaEquipe(@PathVariable String equipeId, @PathVariable String membroId, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            getEquipesService.findByIdAndVerifyMembership(equipeId, userDetails.getUsername());
            removeMembroEquipeService.execute(equipeId, membroId);
            return ResponseEntity.ok("Membro removido com sucesso");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}