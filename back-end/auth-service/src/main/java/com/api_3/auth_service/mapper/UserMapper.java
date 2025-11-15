package com.api_3.auth_service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api_3.auth_service.dto.response.AuthResponse;
import com.api_3.auth_service.model.entity.Task;
import com.api_3.auth_service.model.entity.Teams;
import com.api_3.auth_service.model.entity.User;
import com.api_3.auth_service.repository.ProjectsRepository;

@Component
public class UserMapper {

    @Autowired
    private ProjectsRepository projectsRepository;

    public AuthResponse.UserInfo toUserInfo(User user, List<Teams> teams, List<Task> tasks) {
        List<AuthResponse.TeamInfo> teamInfos = teams.stream()
                .map(t -> new AuthResponse.TeamInfo(t.getUuid(), t.getName()))
                .collect(Collectors.toList());

        List<AuthResponse.TaskInfo> taskInfos;
        if (tasks != null && !tasks.isEmpty()) {
            taskInfos = tasks.stream()
                .map(task -> new AuthResponse.TaskInfo(
                    task.getUuid(),
                    task.getTitle(),
                    task.getStatus() != null ? task.getStatus().name() : null,
                    task.getPriority() != null ? task.getPriority().name() : null,
                    task.getTeamUuid(),
                    task.getProjectUuid(),
                    task.getDueDate()
                ))
                .collect(Collectors.toList());
        } else if (user.getTasks() != null && !user.getTasks().isEmpty()) {
            taskInfos = user.getTasks().stream()
                .map(tu -> new AuthResponse.TaskInfo(
                    tu.getUuid(),
                    tu.getTitle(),
                    tu.getStatus() != null ? tu.getStatus().name() : null,
                    tu.getPriority() != null ? tu.getPriority().name() : null,
                    tu.getTeamUuid(),
                    tu.getProjectUuid(),
                    tu.getDueDate()
                ))
                .collect(Collectors.toList());
        } else {
            taskInfos = java.util.List.of();
        }

        List<AuthResponse.ProjectInfo> projectInfos = teams.stream()
            .flatMap(t -> projectsRepository.findByTeamUuid(t.getUuid()).stream())
            .map(p -> new AuthResponse.ProjectInfo(p.getUuid(), p.getName(), p.isActive(), p.getTeamUuid()))
            .collect(Collectors.toList());

        return new AuthResponse.UserInfo(
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getImg(),
                teamInfos,
                projectInfos,
                taskInfos
        );
    }
}
