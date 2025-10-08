package com.api_3.api_3.controller;

import com.api_3.api_3.dto.request.CreateTaskRequest;
import com.api_3.api_3.dto.request.UpdateTaskRequest;
import com.api_3.api_3.dto.response.TaskResponse;
import com.api_3.api_3.mapper.TaskMapper;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.service.GetEquipesService;
import com.api_3.api_3.service.CreateTaskService;
import com.api_3.api_3.service.DeleteTaskService;
import com.api_3.api_3.service.GetTaskService;
import com.api_3.api_3.service.UpdateTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired private CreateTaskService createTaskService;
    @Autowired private GetTaskService getTaskService;
    @Autowired private UpdateTaskService updateTaskService;
    @Autowired private DeleteTaskService deleteTaskService;

    
    @Autowired private GetEquipesService getEquipesService;
    @Autowired private TaskMapper taskMapper;

    private UserDetails getUserDetails(Authentication authentication) {
        return (UserDetails) authentication.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            getEquipesService.findByIdAndVerifyMembership(request.getEquip_uuid(), userDetails.getUsername());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task savedTask = createTaskService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toTaskResponse(savedTask));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            Task task = getTaskService.findById(uuid);
            getEquipesService.findByIdAndVerifyMembership(task.getEquip_uuid(), userDetails.getUsername());
            return ResponseEntity.ok(taskMapper.toTaskResponse(task));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/count-by-status")
    public ResponseEntity<List<Map<String, Object>>> getTaskCountByStatus() {
        List<Map<String, Object>> counts = getTaskService.countByStatus();
        return ResponseEntity.ok(counts);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String uuid, @Valid @RequestBody UpdateTaskRequest request, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        Task existingTask = getTaskService.findById(uuid);

        try {
            getEquipesService.findByIdAndVerifyMembership(existingTask.getEquip_uuid(), userDetails.getUsername());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task updatedTask = updateTaskService.execute(uuid, request);
        return ResponseEntity.ok(taskMapper.toTaskResponse(updatedTask));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        Task task = getTaskService.findById(uuid);

        try {
            getEquipesService.findByIdAndVerifyMembership(task.getEquip_uuid(), userDetails.getUsername());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task deletedTask = deleteTaskService.execute(uuid);
        return ResponseEntity.ok(taskMapper.toTaskResponse(deletedTask));
    }
}