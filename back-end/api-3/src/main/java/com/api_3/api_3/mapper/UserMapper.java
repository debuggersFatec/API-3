package com.api_3.api_3.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.dto.response.UserResponse;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;

@Component
public class UserMapper {

    @Autowired
    private ProjectsRepository projectsRepository;

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse dto = new UserResponse();
        dto.setUuid(user.getUuid());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setImg(user.getImg());
    dto.setEquipeIds(user.getEquipeIds());
        return dto;
    }

    public List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    // Map user info with Teams (new model)
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
                    task.getEquip_uuid(),
                    task.getProjectUuid(),
                    task.getDue_date()
                ))
                .collect(Collectors.toList());
        } else if (user.getTasks() != null && !user.getTasks().isEmpty()) {
            // Fallback: map embedded TaskUser refs directly if repository didn't return tasks
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