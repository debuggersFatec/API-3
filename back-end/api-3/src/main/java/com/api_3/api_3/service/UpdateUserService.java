package com.api_3.api_3.service;

import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User addEquipeToUser(String userId, String equipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o ID: " + userId));
        if (!user.getEquipeIds().contains(equipeId)) {
            user.getEquipeIds().add(equipeId);
            userRepository.save(user);
        }
        return user;
    }
}
