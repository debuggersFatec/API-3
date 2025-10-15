package com.api_3.api_3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class LeaveTeamService {

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private TaskMaintenanceService taskMaintenanceService; // Injetamos o serviço existente

    @Transactional
    public void execute(String teamUuid, String userUuid) {
        Teams team = teamsRepository.findById(teamUuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + teamUuid));

        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o ID: " + userUuid));

        //  Validar se o utilizador é membro da equipa
        boolean isMember = team.getMembers().stream().anyMatch(m -> m.getUuid().equals(userUuid));
        if (!isMember) {
            throw new SecurityException("Acesso negado. O utilizador não é membro desta equipa.");
        }

        // Remover o utilizador da lista de membros da equipa
        team.getMembers().removeIf(member -> member.getUuid().equals(userUuid));
        teamsRepository.save(team);

        // Remover a equipa da lista de equipas do utilizador
        user.getTeams().removeIf(teamRef -> teamRef.getUuid().equals(teamUuid));
        userRepository.save(user);

        // Remover o utilizador de todos os projetos associados à equipa (NOVA LÓGICA)
        List<Projects> projects = projectsRepository.findByTeamUuid(teamUuid);
        for (Projects project : projects) {
            project.getMembers().removeIf(member -> member.getUuid().equals(userUuid));
            projectsRepository.save(project);
        }

        // Chamar o serviço de manutenção para desatribuir as tarefas (REUTILIZAÇÃO)
        taskMaintenanceService.unassignForTeam(teamUuid, userUuid);

        // Se a equipa ficar sem membros, removê-la do banco
        if (team.getMembers() == null || team.getMembers().isEmpty()) {
            teamsRepository.deleteById(teamUuid);
            // Opcional: remover/arquivar projetos e tarefas associados ao time
            // Mantido simples conforme requisito: apenas excluir o Team quando sem participantes
        }
    }
}