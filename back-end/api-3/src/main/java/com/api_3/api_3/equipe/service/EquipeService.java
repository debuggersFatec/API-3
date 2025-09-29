package com.api_3.api_3.equipe.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isUserMemberOfEquipe(String userEmail, String equipeId) {
        if (userEmail == null || userEmail.isEmpty()) {
            return false;
        }
        
        if (equipeId == null || equipeId.isEmpty()) {
            return false;
        }
        
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (user.getEquipeIds() == null) {
                return false;
            }
            
            // Verificar se a equipe existe
            boolean equipeExists = equipeRepository.existsById(equipeId);
            if (!equipeExists) {
                return false;
            }
            
            // Verificar se a lista está vazia
            if (user.getEquipeIds() == null || user.getEquipeIds().isEmpty()) {
                return false;
            }
            
            // Verificação exata
            for (String id : user.getEquipeIds()) {
                if (id.equals(equipeId)) {
                    return true;
                }
            }
            
            // Verificação com trim() para remover espaços extras
            for (String id : user.getEquipeIds()) {
                if (id.trim().equals(equipeId.trim())) {
                    return true;
                }
            }
            
            // Verificação insensível a maiúsculas/minúsculas como fallback
            for (String id : user.getEquipeIds()) {
                if (id.equalsIgnoreCase(equipeId)) {
                    return true;
                }
            }
            
            return false;
        }
        return false;
    }
}