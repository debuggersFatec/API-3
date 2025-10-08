package com.api_3.api_3.service;

import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GetEquipesService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Equipe> findAllForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado."));

        if (user.getEquipeIds() == null || user.getEquipeIds().isEmpty()) {
            return new ArrayList<>();
        }

        return equipeRepository.findAllById(user.getEquipeIds());
    }

    public Equipe findByIdAndVerifyMembership(String uuid, String userEmail) {
        // Agora este método chama a lógica de verificação completa
        if (!isUserMemberOfEquipe(userEmail, uuid)) {
            throw new SecurityException("Acesso negado à equipe.");
        }

        return equipeRepository.findById(uuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + uuid));
    }

    /**
     * Verifica se um usuário (pelo email) é membro de uma equipe específica (pelo ID).
     * Esta é uma lógica de negócio de leitura, por isso pertence a este serviço.
     */
    private boolean isUserMemberOfEquipe(String userEmail, String equipeId) {
        if (userEmail == null || equipeId == null) {
            return false;
        }

        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        if (user.getEquipeIds() == null || user.getEquipeIds().isEmpty()) {
            return false;
        }

        // A forma mais eficiente de verificar se a lista contém o ID
        return user.getEquipeIds().contains(equipeId);
    }
}