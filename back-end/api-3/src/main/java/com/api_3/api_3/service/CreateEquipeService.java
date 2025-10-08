package com.api_3.api_3.service;

import com.api_3.api_3.dto.request.CreateEquipeRequest;
import com.api_3.api_3.exception.EquipeBadRequestException;
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
import java.util.List;
import java.util.UUID;

@Service
public class CreateEquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Equipe execute(CreateEquipeRequest request, String userEmail) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new EquipeBadRequestException("O nome da equipa é obrigatório.");
        }

        User criador = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilizador criador não foi encontrado."));

        Equipe novaEquipe = new Equipe();
        novaEquipe.setName(request.getName());
        novaEquipe.setUuid(UUID.randomUUID().toString());

        Membro primeiroMembro = new Membro();
        primeiroMembro.setUuid(criador.getUuid());
        primeiroMembro.setName(criador.getName());
        primeiroMembro.setImg(criador.getImg());

        novaEquipe.setMembros(new ArrayList<>(List.of(primeiroMembro)));

        Equipe equipeSalva = equipeRepository.save(novaEquipe);

        criador.getEquipeIds().add(equipeSalva.getUuid());
        userRepository.save(criador);

        return equipeSalva;
    }
}