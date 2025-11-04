package com.api_3.api_3.service.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class GetUserService {
    private static final Logger log = LoggerFactory.getLogger(GetUserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamsRepository teamsRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserMapper userMapper;

    public List<User> findAllUsersForDebug() {
        return userRepository.findAll();
    }

    public AuthResponse.UserInfo findCurrentUserProfile(String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilizador autenticado não encontrado com o email: " + userEmail));

        List<Teams> teams = teamsRepository.findAllById(user.getEquipeIds());
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());
        if (tasks != null && !tasks.isEmpty()) {
            log.debug("GetUserService.findCurrentUserProfile: tasks from responsibleUuid -> {}", tasks.size());
        }

        return userMapper.toUserInfo(user, teams, tasks);
    }
}
