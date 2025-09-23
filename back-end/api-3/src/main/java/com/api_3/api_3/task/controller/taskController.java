package com.api_3.api_3.task.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.api_3.api_3.task.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class taskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    // CREATE -> Criar uma nova task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task newTask) {
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
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE -> Deletar uma tarefa
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Task> deleteTask(@PathVariable String uuid) {
        return taskService.deleteTask(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}