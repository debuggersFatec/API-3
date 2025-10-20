package com.api_3.api_3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class DeleteTaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Task execute(String uuid) {
        Task task = taskRepository.findById(uuid)
            .orElseThrow(() -> new TaskNotFoundException("Falha ao apagar a tarefa com o ID: " + uuid));

        moveTaskToProjectTrashcan(task);
        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            removeTaskFromUser(uuid, task.getResponsible().uuid());
        }

        task.setStatus(Task.Status.DELETED);
        return taskRepository.save(task);
    }

    private void moveTaskToProjectTrashcan(Task task) {
        String projectUuid = task.getProjectUuid();
        if (projectUuid == null || projectUuid.isBlank()) return;

        projectsRepository.findById(projectUuid).ifPresent(project -> {
            if (project.getTasks() == null) project.setTasks(new java.util.ArrayList<>());
            if (project.getTrashcan() == null) project.setTrashcan(new java.util.ArrayList<>());

            // remove from active tasks
            project.getTasks().removeIf(ref -> ref != null && safeEq(ref.uuid(), task.getUuid()));

            // add to trashcan
            Projects updated = project; // just alias for readability
            updated.getTrashcan().add(task.toProjectRef());

            projectsRepository.save(updated);
        });
    }

    private boolean safeEq(String a, String b) {
        return a != null && a.equals(b);
    }

    private void removeTaskFromUser(String taskUuid, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null && !user.getTasks().isEmpty()) {
                boolean removed = user.getTasks().removeIf(tu -> tu != null && safeEq(tu.uuid(), taskUuid));
                if (removed) userRepository.save(user);
            }
        });
    }
}