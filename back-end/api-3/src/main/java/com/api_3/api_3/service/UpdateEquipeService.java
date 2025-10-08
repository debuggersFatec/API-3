package com.api_3.api_3.service;

import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.repository.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateEquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Transactional
    public Equipe execute(String uuid, Equipe equipeAtualizada) {
        // Busca a equipe existente no banco de dados
        Equipe equipeExistente = equipeRepository.findById(uuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada para atualizar com o ID: " + uuid));

        // Atualiza os campos da equipe com os novos dados
        equipeExistente.setName(equipeAtualizada.getName());
        equipeExistente.setMembros(equipeAtualizada.getMembros());
        equipeExistente.setTasks(equipeAtualizada.getTasks());

        // Salva e retorna a equipe atualizada
        return equipeRepository.save(equipeExistente);
    }
}