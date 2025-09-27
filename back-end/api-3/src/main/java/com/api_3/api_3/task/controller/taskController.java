package com.api_3.api_3.task.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.api_3.api_3.equipe.service.EquipeService;
import com.api_3.api_3.task.model.Task;
import com.api_3.api_3.task.repository.TaskRepository;
import com.api_3.api_3.task.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class taskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private EquipeService equipeService;

    // CREATE -> Criar uma nova task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task newTask, Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Object principal = authentication.getPrincipal();
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            if (!(principal instanceof UserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UserDetails userDetails = (UserDetails) principal;
            String userEmail = userDetails.getUsername();

            if (newTask.getEquip_uuid() == null || newTask.getEquip_uuid().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            
            String cleanEquipUuid = newTask.getEquip_uuid().trim();
            if (!cleanEquipUuid.equals(newTask.getEquip_uuid())) {
                newTask.setEquip_uuid(cleanEquipUuid);
            }
            
            boolean isMember = equipeService.isUserMemberOfEquipe(userEmail, newTask.getEquip_uuid());
            
            if (!isMember) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Task savedTask = taskService.createTask(newTask);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // READ -> Obter todas as tarefas
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }

    // READ -> Obter uma tarefa por UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<Task> getTaskById(@PathVariable String uuid) {
        Optional<Task> task = taskRepository.findById(uuid);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // READ -> Obter a contagem de tarefas por status
    @GetMapping("/count-by-status")
    public ResponseEntity<List<Map<String, Object>>> getTaskCountByStatus() {
        List<Map<String, Object>> counts = taskRepository.countByStatus();
        return ResponseEntity.ok(counts);
    }


    // UPDATE -> Atualiza uma tarefa existente
    @PutMapping("/{uuid}")
    public ResponseEntity<Task> updateTask(@PathVariable String uuid, @RequestBody Task updatedTask, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        // Validação: Verificar se a tarefa existe
        Optional<Task> taskOptional = taskRepository.findById(uuid);
        if (taskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Validação: Verificar se o usuário pertence à equipe da tarefa
        Task task = taskOptional.get();
        if (!equipeService.isUserMemberOfEquipe(userEmail, task.getEquip_uuid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Chama o serviço para realizar a atualização
        return taskService.updateTask(uuid, updatedTask)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE -> Deletar uma tarefa
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Task> deleteTask(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        Optional<Task> taskOptional = taskRepository.findById(uuid);
        if (taskOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Task task = taskOptional.get();
        if (!equipeService.isUserMemberOfEquipe(userEmail, task.getEquip_uuid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return taskService.deleteTask(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}