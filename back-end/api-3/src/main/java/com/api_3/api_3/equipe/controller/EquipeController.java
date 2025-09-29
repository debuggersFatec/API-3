package com.api_3.api_3.equipe.controller;
 
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.equipe.model.Membro;
import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.exception.EquipeBadRequestException;
import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.MemberAlreadyExistsException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;
 
@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    //MÉTODOS DE GESTÃO DA EQUIPE
    @PostMapping
    public ResponseEntity<Equipe> createEquipe(@RequestBody Equipe novaEquipe, Authentication authentication) {
        if (novaEquipe == null || novaEquipe.getName() == null || novaEquipe.getName().trim().isEmpty()) {
            throw new EquipeBadRequestException("O nome da equipa é obrigatório.");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User criador = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilizador criador não foi encontrado."));

        novaEquipe.setUuid(UUID.randomUUID().toString());

        Membro primeiroMembro = new Membro();
        primeiroMembro.setUuid(criador.getUuid());
        primeiroMembro.setName(criador.getName());
        primeiroMembro.setImg(criador.getImg());

        novaEquipe.setMembros(new ArrayList<>(List.of(primeiroMembro)));
        
        Equipe equipeSalva = equipeRepository.save(novaEquipe);

        criador.getEquipeIds().add(equipeSalva.getUuid());
        userRepository.save(criador);

        return ResponseEntity.status(HttpStatus.CREATED).body(equipeSalva);
    }

    @GetMapping
    public ResponseEntity<List<Equipe>> getEquipesDoUsuario(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado."));
        
        if (user.getEquipeIds() == null || user.getEquipeIds().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        List<Equipe> equipes = equipeRepository.findAllById(user.getEquipeIds());
        return ResponseEntity.ok(equipes);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Equipe> getEquipeById(@PathVariable String uuid, Authentication authentication) {
        Equipe equipe = equipeRepository.findById(uuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + uuid));
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado."));
        
        boolean isMembro = user.getEquipeIds() != null && user.getEquipeIds().contains(uuid);
        if (!isMembro) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(equipe);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Equipe> updateEquipe(@PathVariable String uuid, @RequestBody Equipe equipeAtualizada) {
        Equipe equipeExistente = equipeRepository.findById(uuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada para atualizar com o ID: " + uuid));
            
        equipeExistente.setName(equipeAtualizada.getName());
        equipeExistente.setMembros(equipeAtualizada.getMembros());
        equipeExistente.setTasks(equipeAtualizada.getTasks());
        
        Equipe savedEquipe = equipeRepository.save(equipeExistente);
        return ResponseEntity.ok(savedEquipe);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEquipe(@PathVariable String uuid) {
        Equipe equipe = equipeRepository.findById(uuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada para apagar com o ID: " + uuid));
        
        if (equipe.getMembros() != null) {
            for (Membro membro : equipe.getMembros()) {
                userRepository.findById(membro.getUuid()).ifPresent(user -> {
                    if (user.getEquipeIds() != null) {
                        user.getEquipeIds().remove(uuid);
                        userRepository.save(user);
                    }
                });
            }
        }
        
        equipeRepository.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    // MÉTODOS DE GESTÃO DE MEMBROS

    @PostMapping("/{equipeId}/membros/{membroId}")
    public ResponseEntity<String> createEquipeMembro(@PathVariable String equipeId, @PathVariable String membroId) {
        Equipe equipe = equipeRepository.findById(equipeId)
            .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + equipeId));

        User membro = userRepository.findById(membroId)
            .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o ID: " + membroId));

        boolean membroJaExiste = equipe.getMembros() != null && 
                                equipe.getMembros().stream().anyMatch(m -> m.getUuid().equals(membroId));
        
        if (membroJaExiste) {
            throw new MemberAlreadyExistsException("O utilizador já é membro desta equipa.");
        }

        Membro novoMembro = new Membro();
        novoMembro.setUuid(membro.getUuid());
        novoMembro.setName(membro.getName());
        novoMembro.setImg(membro.getImg());
        
        if (equipe.getMembros() == null) {
            equipe.setMembros(new ArrayList<>());
        }
        equipe.getMembros().add(novoMembro);
        equipeRepository.save(equipe);

        if (membro.getEquipeIds() == null) {
            membro.setEquipeIds(new ArrayList<>());
        }
        if (!membro.getEquipeIds().contains(equipeId)) {
            membro.getEquipeIds().add(equipeId);
            userRepository.save(membro);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Membro adicionado com sucesso");
    }

    @DeleteMapping("/{equipeId}/membros/{membroId}")
    public ResponseEntity<String> removeEquipeMembro(@PathVariable String equipeId, @PathVariable String membroId) {
        Equipe equipe = equipeRepository.findById(equipeId)
            .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + equipeId));

        User membroParaRemover = userRepository.findById(membroId)
            .orElseThrow(() -> new UserNotFoundException("Utilizador a remover não encontrado com o ID: " + membroId));

        boolean membroEncontrado = equipe.getMembros() != null && 
                                   equipe.getMembros().removeIf(m -> m.getUuid().equals(membroId));
        
        if (!membroEncontrado) {
            throw new UserNotFoundException("Membro não encontrado na equipa.");
        }
        
        equipeRepository.save(equipe);
        
        if (membroParaRemover.getEquipeIds() != null) {
            membroParaRemover.getEquipeIds().remove(equipeId);
            userRepository.save(membroParaRemover);
        }
        
        return ResponseEntity.status(HttpStatus.OK).body("Membro removido com sucesso");
    }
}