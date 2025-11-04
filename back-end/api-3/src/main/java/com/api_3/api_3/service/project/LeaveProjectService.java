package com.api_3.api_3.service.project;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.NotificationService;
import com.api_3.api_3.service.task.TaskMaintenanceService;

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

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void execute(String projectUuid, String userUuid) {
        Projects project = projectsRepository.findById(projectUuid)
                .orElseThrow(() -> new ProjectNotFoundException("Project não encontrado com o ID: " + projectUuid));

        boolean isMember = project.getMembers().stream().anyMatch(m -> m.getUuid().equals(userUuid));
        if (!isMember) {
            throw new SecurityException("Acesso negado. O utilizador não é membro deste projeto.");
        }

        project.getMembers().removeIf(member -> member.getUuid().equals(userUuid));
    projectsRepository.save(project);

        List<Task> tasks = taskRepository.findByProjectUuidAndResponsibleUuid(projectUuid, userUuid);
        if (tasks != null && !tasks.isEmpty()) {
            for (Task t : tasks) {
                t.setResponsible(null);
            }
            taskRepository.saveAll(tasks);
        }

        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                user.getTasks().removeIf(tRef -> projectUuid.equals(tRef.getProjectUuid()));
            }
            userRepository.save(user);
        });

        taskMaintenanceService.unassignForProject(projectUuid, userUuid);

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
        } else {
            notificationService.notifyProjectMemberLeft(projectUuid, userUuid);
        }
    }
}
