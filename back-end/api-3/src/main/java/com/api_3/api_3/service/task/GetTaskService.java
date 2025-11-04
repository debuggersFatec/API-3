package com.api_3.api_3.service.task;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.TaskRepository;

@Service
public class GetTaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(String uuid) {
        return taskRepository.findById(uuid)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com o ID: " + uuid));
    }

    public List<Map<String, Object>> countByStatus() {
        return taskRepository.countByStatus();
    }
}
