package com.api_3.api_3.equipe.controller;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind .annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.equipe.model.Membro;
import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;
 
@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EquipeRepository equipeRepository;


    // CREATE -> Criar uma nova equipe
    @PostMapping
    public ResponseEntity<Equipe> createEquipe(@RequestBody Equipe novaEquipe , Authentication authentication){
        // 1. Verificar se o usuário está autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();
        System.out.println("DEBUG: Criando equipe para o usuário com email: " + userEmail);
        
        User criador = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        System.out.println("DEBUG: Usuário criador encontrado: " + criador.getName() + ", UUID: " + criador.getUuid());
        System.out.println("DEBUG: Equipes do usuário ANTES: " + criador.getEquipeIds());

        // 2. Configurar o UUID da nova equipe
        novaEquipe.setUuid(UUID.randomUUID().toString());
        System.out.println("DEBUG: UUID gerado para a nova equipe: " + novaEquipe.getUuid());

        // 3. Criar o primeiro membro da equipe com os dados do criador
        Membro primeiroMembro = new Membro();
        primeiroMembro.setUuid(criador.getUuid());
        primeiroMembro.setName(criador.getName());
        primeiroMembro.setImg(criador.getImg());
        primeiroMembro.setAtribuidas_tasks(0);
        primeiroMembro.setConcluidas_tasks(0);
        primeiroMembro.setVencidas_tasks(0);

        // 4. Adicionar o criador como o primeiro membro da equipa
    novaEquipe.setMembros(new ArrayList<>(List.of(primeiroMembro)));
    
    // 5. Salvar a nova equipa na base de dados
    Equipe equipeSalva = equipeRepository.save(novaEquipe);
    System.out.println("DEBUG: Equipe salva com sucesso. UUID: " + equipeSalva.getUuid());

    // 6. ADICIONADO: Atualizar o documento do utilizador para adicionar o ID da nova equipa
    criador.getEquipeIds().add(equipeSalva.getUuid());
    userRepository.save(criador);
    System.out.println("DEBUG: Equipes do usuário DEPOIS: " + criador.getEquipeIds());

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