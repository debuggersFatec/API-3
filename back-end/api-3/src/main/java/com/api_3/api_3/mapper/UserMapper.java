package com.api_3.api_3.mapper;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.dto.response.UserResponse;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

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
        List<AuthResponse.EquipeInfo> equipeInfos = equipes.stream()
                .map(equipe -> new AuthResponse.EquipeInfo(equipe.getUuid(), equipe.getName()))
                .collect(Collectors.toList());
        
        List<AuthResponse.TaskInfo> taskInfos = tasks.stream()
                .map(task -> new AuthResponse.TaskInfo(
                        task.getUuid(),
                        task.getTitle(),
                        task.getStatus(),
                        task.getPriority(),
                        task.getEquip_uuid(),
                        task.getDue_date()
                ))
                .collect(Collectors.toList());

        return new AuthResponse.UserInfo(
                user.getUuid(),
                user.getName(),
                user.getEmail(),
                user.getImg(),
                equipeInfos,
                taskInfos
        );
    }
}