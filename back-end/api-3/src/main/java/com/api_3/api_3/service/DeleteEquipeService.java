package com.api_3.api_3.service;

import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Membro;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteEquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void execute(String uuid) {
        Equipe equipe = equipeRepository.findById(uuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada para apagar com o ID: " + uuid));

        if (equipe.getMembros() != null) {
            for (Membro membro : equipe.getMembros()) {
                userRepository.findById(membro.getUuid()).ifPresent(user -> {
                    if (user.getEquipeIds() != null) {
                        user.getEquipeIds().remove(uuid);
                        userRepository.save(user);
                    }
                });
            }
        }

        equipeRepository.deleteById(uuid);
    }
}