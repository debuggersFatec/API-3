package com.api_3.api_3.service;

import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveMembroEquipeService {

    @Autowired
    private EquipeRepository equipeRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void execute(String equipeId, String membroId) {
        Equipe equipe = equipeRepository.findById(equipeId)
            .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + equipeId));

        boolean membroEncontrado = equipe.getMembros() != null &&
                                   equipe.getMembros().removeIf(m -> m.getUuid().equals(membroId));
        
        if (!membroEncontrado) {
            throw new UserNotFoundException("Membro não encontrado na equipa.");
        }
        
        equipeRepository.save(equipe);
        
        userRepository.findById(membroId).ifPresent(membroParaRemover -> {
            if (membroParaRemover.getEquipeIds() != null) {
                membroParaRemover.getEquipeIds().remove(equipeId);
                userRepository.save(membroParaRemover);
            }
        });
    }
}