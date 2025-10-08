package com.api_3.api_3.service;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserMapper userMapper;

    public List<User> findAllUsersForDebug() {
        return userRepository.findAll();
    }

    public AuthResponse.UserInfo findCurrentUserProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilizador autenticado não encontrado com o email: " + userEmail));

        List<Equipe> equipes = equipeRepository.findAllById(user.getEquipeIds());
        List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());

        return userMapper.toUserInfo(user, equipes, tasks);
    }
}