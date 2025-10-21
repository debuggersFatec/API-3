package com.api_3.api_3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class GetUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamsRepository teamsRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectsRepository projectsRepository;

    public List<User> findAllUsersForDebug() {
        return userRepository.findAll();
    }

    public AuthResponse.UserInfo findCurrentUserProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilizador autenticado não encontrado com o email: " + userEmail));

        List<Teams> teams = teamsRepository.findAllById(user.getEquipeIds());
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());
        if ((tasks == null || tasks.isEmpty()) && user.getTasks() != null && !user.getTasks().isEmpty()) {
            java.util.List<String> ids = user.getTasks().stream()
                    .map(t -> t.uuid())
                    .filter(java.util.Objects::nonNull)
                    .toList();
            if (!ids.isEmpty()) {
                tasks = taskRepository.findAllById(ids);
            }
        }
        if (tasks == null || tasks.isEmpty()) {
            java.util.List<String> taskIds = teams.stream()
                .flatMap(t -> projectsRepository.findByTeamUuid(t.getUuid()).stream())
                .filter(p -> p.getTasks() != null)
                .flatMap(p -> p.getTasks().stream())
                .filter(tp -> tp != null && tp.getResponsible() != null && user.getUuid().equals(tp.getResponsible().uuid()))
                .map(tp -> tp.getUuid())
                .distinct()
                .toList();
            if (!taskIds.isEmpty()) {
                tasks = taskRepository.findAllById(taskIds);
            }
        }
        if (tasks == null || tasks.isEmpty()) {
            java.util.List<String> allTaskIds = teams.stream()
                .flatMap(t -> projectsRepository.findByTeamUuid(t.getUuid()).stream())
                .filter(p -> p.getTasks() != null)
                .flatMap(p -> p.getTasks().stream())
                .filter(tp -> tp != null && tp.getStatus() != null && tp.getStatus() != Task.Status.DELETED)
                .map(tp -> tp.getUuid())
                .distinct()
                .toList();
            if (!allTaskIds.isEmpty()) {
                tasks = taskRepository.findAllById(allTaskIds);
            }
        }

        return userMapper.toUserInfo(user, teams, tasks);
    }
}