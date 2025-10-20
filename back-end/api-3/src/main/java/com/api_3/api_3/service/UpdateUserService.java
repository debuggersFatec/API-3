package com.api_3.api_3.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.dto.request.UpdateUserRequest;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.UserRepository;

@Service
public class UpdateUserService {

    private final UserRepository userRepository;

    public UpdateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User updateCurrentUser(UpdateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }
        String email = auth.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }
        if (request.getImg() != null && !request.getImg().isBlank()) {
            user.setImg(request.getImg().trim());
        }
        return userRepository.save(user);
    }
}
