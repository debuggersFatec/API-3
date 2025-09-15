package com.api_3.api_3.task.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.task.model.Task;
import com.api_3.api_3.task.repository.TaskRepository;

@RestController
@RequestMapping("/api/tasks")
public class taskController {

    @Autowired
    private TaskRepository taskRepository;

    // CREATE -> Criar uma nova task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task newTask) {
        newTask.setUuid(UUID.randomUUID().toString());
        Task savedTask = taskRepository.save(newTask);
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
        Optional<Task> task = taskRepository.findById(uuid);
        return task.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // UPDATE -> Atualiza uma tarefa existente
    @PutMapping("/{uuid}")
    public ResponseEntity<Task> updateTask(@PathVariable String uuid, @RequestBody Task updatedTask) {
        return taskRepository.findById(uuid).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setDue_date(updatedTask.getDue_date());
            task.setStatus(updatedTask.getStatus());
            task.setPriority(updatedTask.getPriority());
            // ... Mais campos para atualização posteriormente

            Task savedTask = taskRepository.save(task);
            return ResponseEntity.ok(savedTask);
        }).orElse(ResponseEntity.notFound().build()); // Ponto e vírgula adicionado
    }

    // DELETE -> Deletar uma tarefa
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTask(@PathVariable String uuid) {
        if (!taskRepository.existsById(uuid)) {
            return ResponseEntity.notFound().build();
        }
        taskRepository.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }
}