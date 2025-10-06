package com.api_3.api_3.task.controller;

import java.util.List;
import java.util.Map;
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

import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.equipe.service.EquipeService;
import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.exception.TaskValidationException;
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

    @Autowired
    private EquipeRepository equipeRepository;

    // CREATE -> Criar uma nova task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task newTask, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        String equipUuid = newTask.getEquip_uuid();
        if (equipUuid == null || equipUuid.trim().isEmpty()) {
            throw new TaskValidationException("O ID da equipe é obrigatório para criar uma tarefa.");
        }

        equipeRepository.findById(equipUuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipe com ID " + equipUuid + " não encontrada."));

        boolean isMember = equipeService.isUserMemberOfEquipe(userEmail, equipUuid);
        if (!isMember) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task savedTask = taskService.createTask(newTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
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
        Task task = taskRepository.findById(uuid)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com o ID: " + uuid));
        return ResponseEntity.ok(task);
    }

    // READ -> Obter a contagem de tarefas por status
    @GetMapping("/count-by-status")
    public ResponseEntity<List<Map<String, Object>>> getTaskCountByStatus() {
        List<Map<String, Object>> counts = taskRepository.countByStatus();
        return ResponseEntity.ok(counts);
    }


    // UPDATE -> Atualiza uma tarefa existente por ID do usuario
    @PutMapping("/{uuid}")
    public ResponseEntity<Task> updateTask(@PathVariable String uuid, @RequestBody Task updatedTask, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        Task task = taskRepository.findById(uuid)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada para atualizar com o ID: " + uuid));

        if (!equipeService.isUserMemberOfEquipe(userEmail, task.getEquip_uuid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return taskService.updateTask(uuid, updatedTask)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException("Falha ao atualizar a tarefa com ID: " + uuid));
    }

    // DELETE -> Deletar uma tarefa
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Task> deleteTask(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        Task task = taskRepository.findById(uuid)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada para apagar com o ID: " + uuid));

        if (!equipeService.isUserMemberOfEquipe(userEmail, task.getEquip_uuid())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return taskService.deleteTask(uuid)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TaskNotFoundException("Falha ao apagar a tarefa com ID: " + uuid));
    }
}