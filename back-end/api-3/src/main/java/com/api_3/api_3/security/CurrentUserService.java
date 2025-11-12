package com.api_3.api_3.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.UserRepository;

@Component
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String currentUserUuid() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return null;
            String username = null;
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails ud) {
                username = ud.getUsername();
            } else {
                username = auth.getName();
            }
            if (username == null) return null;
            return userRepository.findByEmailIgnoreCase(username)
                    .map(User::getUuid)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}
