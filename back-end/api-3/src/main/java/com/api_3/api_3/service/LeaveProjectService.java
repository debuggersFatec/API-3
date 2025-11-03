package com.api_3.api_3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class LeaveProjectService {

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMaintenanceService taskMaintenanceService;

    @Transactional
    public void execute(String projectUuid, String userUuid) {
        Projects project = projectsRepository.findById(projectUuid)
                .orElseThrow(() -> new ProjectNotFoundException("Project não encontrado com o ID: " + projectUuid));

        // validar se o utilizador é membro do projeto
        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getUuid().equals(userUuid));
        if (!isMember) {
            throw new SecurityException("Acesso negado. O utilizador não é membro deste projeto.");
        }

        // remover utilizador dos membros do projeto
        project.getMembers().removeIf(member -> member.getUuid().equals(userUuid));
        projectsRepository.save(project);

        // buscar apenas as tasks deste projeto que têm o user como responsible
        List<Task> tasks = taskRepository.findByProjectUuidAndResponsibleUuid(projectUuid, userUuid);
        if (tasks != null && !tasks.isEmpty()) {
            for (Task t : tasks) {
                t.setResponsible(null);
            }
            taskRepository.saveAll(tasks);
        }

        // remover referências dessas tasks na lista de tasks do usuário (se existir)
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                user.getTasks().removeIf(tRef -> projectUuid.equals(tRef.getProjectUuid()));
            }
            userRepository.save(user);
        });

        // desatribuir tarefas relacionadas com este projeto para este utilizador
        // (opcional/redundante)
        taskMaintenanceService.unassignForProject(projectUuid, userUuid);

        // se o projeto ficar sem membros, remover o projeto e remover referência no
        // Team
        if (project.getMembers() == null || project.getMembers().isEmpty()) {
            projectsRepository.deleteById(projectUuid);

            if (project.getTeamUuid() != null) {
                teamsRepository.findById(project.getTeamUuid()).ifPresent(team -> {
                    team.setProjects(
                            team.getProjects().stream()
                                    .filter(pr -> !projectUuid.equals(pr.getUuid()))
                                    .collect(Collectors.toList()));
                    teamsRepository.save(team);
                });
            }
        }
    }
}