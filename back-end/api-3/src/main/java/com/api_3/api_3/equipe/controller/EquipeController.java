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
    public ResponseEntity<Equipe> createEquipe(@RequestBody Equipe novaEquipe, Authentication authentication) {
        // Validar entrada
        if (novaEquipe == null || novaEquipe.getName() == null || novaEquipe.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // 1. Verificar se o usuário está autenticado
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();
        
        // 2. Buscar o usuário criador
        User criador = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // 3. Configurar o UUID da nova equipe
        novaEquipe.setUuid(UUID.randomUUID().toString());

        // 4. Criar o primeiro membro da equipe com os dados do criador
        Membro primeiroMembro = new Membro();
        primeiroMembro.setUuid(criador.getUuid());
        primeiroMembro.setName(criador.getName());
        primeiroMembro.setImg(criador.getImg());
        primeiroMembro.setAtribuidas_tasks(0);
        primeiroMembro.setConcluidas_tasks(0);
        primeiroMembro.setVencidas_tasks(0);

        // 5. Adicionar o criador como o primeiro membro da equipe
        novaEquipe.setMembros(new ArrayList<>(List.of(primeiroMembro)));
        
        // 6. Salvar a nova equipe na base de dados
        Equipe equipeSalva = equipeRepository.save(novaEquipe);

        // 7. Atualizar o documento do usuário para adicionar o ID da nova equipe
        criador.getEquipeIds().add(equipeSalva.getUuid());
        userRepository.save(criador);

        return ResponseEntity.status(HttpStatus.CREATED).body(equipeSalva);
    }

    // READ -> Obter as equipes do usuario
    @GetMapping
    public ResponseEntity<List<Equipe>> getEquipesDoUsuario(Authentication authentication) {
        // Validar autenticação
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        // Buscar o usuário para obter os IDs das equipes
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        } 
        
        User user = userOptional.get();
        List<String> equipeIds = user.getEquipeIds();
        
        // Verificar se o usuário tem equipes
        if (equipeIds == null || equipeIds.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>()); // Retornar lista vazia em vez de null
        }
        
        // Buscar todas as equipes com base nos IDs
        List<Equipe> equipes = equipeRepository.findAllById(equipeIds);
        return ResponseEntity.ok(equipes);
    }

    // READ -> Obter uma equipe específica por UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<?> getEquipeById(@PathVariable String uuid, Authentication authentication) {
        // Validar autenticação
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Autenticação necessária para acessar os detalhes da equipe");
        }
        
        // Verificar se o ID fornecido é válido
        if (uuid == null || uuid.isBlank()) {
            return ResponseEntity.badRequest()
                .body("ID da equipe inválido");
        }
        
        // Buscar a equipe no repositório
        Optional<Equipe> equipeOptional = equipeRepository.findById(uuid);
        if (equipeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Equipe não encontrada com o ID: " + uuid);
        }
        
        // Verificar se o usuário atual é membro da equipe
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Não foi possível verificar o usuário atual");
        }
        
        User user = userOptional.get();
        boolean isMembro = user.getEquipeIds() != null && user.getEquipeIds().contains(uuid);
        
        // Se não for membro, retornar acesso negado
        if (!isMembro) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Você não tem permissão para acessar os detalhes desta equipe");
        }
        
        // Retornar a equipe encontrada
        return ResponseEntity.ok(equipeOptional.get());
    }

    //  UPDATE -> Atualiza uma equipe existente (tasks,membros)
    @PutMapping("/{uuid}")
    public ResponseEntity<Equipe> updateEquipe(@PathVariable String uuid, @RequestBody Equipe equipeAtualizada, Authentication authentication) {
        // Validar autenticação
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Verificar se o ID fornecido é válido
        if (uuid == null || uuid.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Validar dados da equipe atualizada
        if (equipeAtualizada == null || equipeAtualizada.getName() == null || equipeAtualizada.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Buscar a equipe existente
        return equipeRepository.findById(uuid).map(equipeExistente -> {
            // Manter o UUID original
            equipeAtualizada.setUuid(uuid);
            
            // Atualizar campos específicos
            equipeExistente.setName(equipeAtualizada.getName());
            
            // Tratar membros com cuidado para não perder informações
            if (equipeAtualizada.getMembros() != null) {
                equipeExistente.setMembros(equipeAtualizada.getMembros());
            }
            
            // Tratar tasks com cuidado para não perder informações
            if (equipeAtualizada.getTasks() != null) {
                equipeExistente.setTasks(equipeAtualizada.getTasks());
            }
            
            // Salvar a equipe atualizada
            Equipe savedEquipe = equipeRepository.save(equipeExistente);
            return ResponseEntity.ok(savedEquipe);
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE -> Deletar uma equipe existente
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteEquipe(@PathVariable String uuid, Authentication authentication) {
        // Validar autenticação
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // Verificar se o ID fornecido é válido
        if (uuid == null || uuid.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Verificar se a equipe existe
        Optional<Equipe> equipeOptional = equipeRepository.findById(uuid);
        if (equipeOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Equipe equipe = equipeOptional.get();
        
        // Remover a equipe das listas de equipes de todos os membros
        if (equipe.getMembros() != null && !equipe.getMembros().isEmpty()) {
            for (Membro membro : equipe.getMembros()) {
                Optional<User> userOptional = userRepository.findById(membro.getUuid());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    if (user.getEquipeIds() != null) {
                        user.getEquipeIds().remove(uuid);
                        userRepository.save(user);
                    }
                }
            }
        }
        
        // Deletar a equipe
        equipeRepository.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
    
    // UPDATE -> Adicionar membro na equipe
    @PostMapping("/{id}/membros/{membroId}")
    public ResponseEntity<String> createEquipeMembro(@PathVariable String id, @PathVariable String membroId, Authentication authentication) {
        // Validar autenticação
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autenticação necessária");
        }
        
        // Verificar se os IDs fornecidos são válidos
        if (id == null || id.isBlank() || membroId == null || membroId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("IDs de equipe e membro são obrigatórios");
        }
        
        // Buscar a equipe pelo ID
        Optional<Equipe> equipeExistenteOptional = equipeRepository.findById(id);
        if (equipeExistenteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipe não encontrada com o ID fornecido");
        }
        Equipe equipe = equipeExistenteOptional.get();

        // Verificar se o membro já existe na equipe (verificando na lista de membros)
        boolean membroJaExiste = equipe.getMembros() != null && 
                                equipe.getMembros().stream()
                                .anyMatch(membro -> membro.getUuid().equals(membroId));
        
        if (membroJaExiste) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("O usuário já é membro desta equipe");
        }

        // Verificar se o ID do membro existe
        Optional<User> membroExistenteOptional = userRepository.findById(membroId);
        if (membroExistenteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado com o ID fornecido");
        }
        
        User membro = membroExistenteOptional.get();

        // Criar novo objeto Membro
        Membro novoMembro = new Membro();
        novoMembro.setUuid(membro.getUuid());
        novoMembro.setName(membro.getName());
        novoMembro.setImg(membro.getImg());
        novoMembro.setAtribuidas_tasks(0);
        novoMembro.setConcluidas_tasks(0);
        novoMembro.setVencidas_tasks(0);
        
        // Inicializar a lista de membros se for nula
        if (equipe.getMembros() == null) {
            equipe.setMembros(new ArrayList<>());
        }
        
        // Adicionar o membro à equipe
        equipe.getMembros().add(novoMembro);
        equipeRepository.save(equipe);

        // Inicializar a lista de equipes do usuário se for nula
        if (membro.getEquipeIds() == null) {
            membro.setEquipeIds(new ArrayList<>());
        }
        
        // Adicionar a equipe ao usuário
        if (!membro.getEquipeIds().contains(id)) {
            membro.getEquipeIds().add(id);
            userRepository.save(membro);
        }

        return ResponseEntity.status(HttpStatus.OK).body("Membro adicionado com sucesso");
    }
    
    // DELETE -> Remover membro da equipe
    @DeleteMapping("/{id}/membros/{membroId}")
    public ResponseEntity<String> removeEquipeMembro(@PathVariable String id, @PathVariable String membroId, Authentication authentication) {
        // Validar autenticação
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Autenticação necessária");
        }
        
        // Verificar se os IDs fornecidos são válidos
        if (id == null || id.isBlank() || membroId == null || membroId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("IDs de equipe e membro são obrigatórios");
        }
        
        // Buscar a equipe pelo ID
        Optional<Equipe> equipeOptional = equipeRepository.findById(id);
        if (equipeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipe não encontrada com o ID fornecido");
        }
        
        Equipe equipe = equipeOptional.get();
        
        // Verificar se existem membros na equipe
        if (equipe.getMembros() == null || equipe.getMembros().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A equipe não possui membros");
        }
        
        // Verificar se o membro existe na equipe
        boolean membroEncontrado = false;
        List<Membro> membrosAtualizados = new ArrayList<>();
        
        for (Membro membro : equipe.getMembros()) {
            if (membro.getUuid().equals(membroId)) {
                membroEncontrado = true;
            } else {
                membrosAtualizados.add(membro);
            }
        }
        
        if (!membroEncontrado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Membro não encontrado na equipe");
        }
        
        // Atualizar a lista de membros da equipe
        equipe.setMembros(membrosAtualizados);
        equipeRepository.save(equipe);
        
        // Remover a equipe da lista de equipes do usuário
        Optional<User> userOptional = userRepository.findById(membroId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getEquipeIds() != null) {
                user.getEquipeIds().remove(id);
                userRepository.save(user);
            }
        }
        
        return ResponseEntity.status(HttpStatus.OK).body("Membro removido com sucesso");
    }

}