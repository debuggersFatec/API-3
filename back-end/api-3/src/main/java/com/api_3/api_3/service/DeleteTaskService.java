package com.api_3.api_3.service;

import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.model.embedded.TaskInfo;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class DeleteTaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Task execute(String uuid) {
        Task task = taskRepository.findById(uuid)
            .orElseThrow(() -> new TaskNotFoundException("Falha ao apagar a tarefa com o ID: " + uuid));

    moveTaskToLixeiraInEquipe(task);
        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            removeTaskFromUser(uuid, task.getResponsible().uuid());
        }

        task.setStatus(Task.Status.DELETED);
        return taskRepository.save(task);
    }

    private void moveTaskToLixeiraInEquipe(Task task) {
        // New architecture: Teams no longer embeds TaskInfo; if a trash concept is needed, implement on Projects or a dedicated collection
    }

    private void removeTaskFromUser(String taskUuid, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                boolean removed = user.getTasks().removeIf(t -> t instanceof Task.TaskUser && ((Task.TaskUser) t).uuid().equals(taskUuid));
                if (removed) {
                    userRepository.save(user);
                }
            }
        });
    }
}