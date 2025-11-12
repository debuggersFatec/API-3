package com.api_3.api_3.service.task;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;

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

        tasks.forEach(t -> t.setResponsible(null));
        taskRepository.saveAll(tasks);

        Set<String> taskIds = tasks.stream().map(Task::getUuid).collect(Collectors.toSet());
        userRepository.findById(userUuid).ifPresent(u -> {
            if (u.getTasks() != null) {
                u.getTasks().removeIf(tu -> taskIds.contains(tu.getUuid()));
                userRepository.save(u);
            }
        });
    }
}
