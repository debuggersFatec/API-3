package com.api_3.api_3.service;

import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskMaintenanceService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskMaintenanceService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void unassignForTeam(String teamUuid, String userUuid) {
        List<Task> tasks = taskRepository.findByTeamUuidAndResponsibleUuid(teamUuid, userUuid);
        unassign(tasks, userUuid);
    }

    @Transactional
    public void unassignForProject(String projectUuid, String userUuid) {
        List<Task> tasks = taskRepository.findByProjectUuidAndResponsibleUuid(projectUuid, userUuid);
        unassign(tasks, userUuid);
    }

    private void unassign(List<Task> tasks, String userUuid) {
        if (tasks == null || tasks.isEmpty()) return;

        // set responsible = null on each task
        tasks.forEach(t -> t.setResponsible(null));
        taskRepository.saveAll(tasks);

        // remove TaskUser refs from the user
        Set<String> taskIds = tasks.stream().map(Task::getUuid).collect(Collectors.toSet());
        userRepository.findById(userUuid).ifPresent(u -> {
            if (u.getTasks() != null) {
                u.getTasks().removeIf(tu -> taskIds.contains(tu.getUuid()));
                userRepository.save(u);
            }
        });
    }
}
