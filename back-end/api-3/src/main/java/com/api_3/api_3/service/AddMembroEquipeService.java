package com.api_3.api_3.service;

import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.MemberAlreadyExistsException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Membro;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class AddMembroEquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void execute(String equipeId, String membroId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + equipeId));

        User membro = userRepository.findById(membroId)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o ID: " + membroId));

        boolean membroJaExiste = equipe.getMembros() != null &&
                equipe.getMembros().stream().anyMatch(m -> m.getUuid().equals(membroId));

        if (membroJaExiste) {
            throw new MemberAlreadyExistsException("O utilizador já é membro desta equipa.");
        }

        Membro novoMembro = new Membro();
        novoMembro.setUuid(membro.getUuid());
        novoMembro.setName(membro.getName());
        novoMembro.setImg(membro.getImg());

        if (equipe.getMembros() == null) {
            equipe.setMembros(new ArrayList<>());
        }
        equipe.getMembros().add(novoMembro);
        equipeRepository.save(equipe);

        if (membro.getEquipeIds() == null) {
            membro.setEquipeIds(new ArrayList<>());
        }
        if (!membro.getEquipeIds().contains(equipeId)) {
            membro.getEquipeIds().add(equipeId);
            userRepository.save(membro);
        }
    }
}