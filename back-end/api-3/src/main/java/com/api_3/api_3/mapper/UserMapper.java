package com.api_3.api_3.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.dto.response.UserResponse;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Task;
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

    public AuthResponse.UserInfo toUserInfo(User user, List<Equipe> equipes, List<Task> tasks) {
        List<AuthResponse.TeamInfo> teamInfos = equipes.stream()
                .map(equipe -> new AuthResponse.TeamInfo(equipe.getUuid(), equipe.getName()))
                .collect(Collectors.toList());

        List<AuthResponse.TaskInfo> taskInfos = tasks.stream()
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

    // Collect projects for the user's teams
    List<AuthResponse.ProjectInfo> projectInfos = equipes.stream()
        .flatMap(eq -> projectsRepository.findByTeamUuid(eq.getUuid()).stream())
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