package com.api_3.api_3.service;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.dto.request.CreateTaskRequest;
import com.api_3.api_3.exception.InvalidResponsibleException;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TeamNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class CreateTaskService {
    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Task execute(CreateTaskRequest request) {
        // Carregar o projeto alvo e derivar a equipe a partir dele
        Projects project = projectsRepository.findById(request.getProject_uuid())
            .orElseThrow(() -> new ProjectNotFoundException("Projeto com ID " + request.getProject_uuid() + " não encontrado."));

        Teams team = teamsRepository.findById(project.getTeamUuid())
            .orElseThrow(() -> new TeamNotFoundException("Team com ID " + project.getTeamUuid() + " não encontrado."));

        Task newTask = new Task();
        newTask.setUuid(UUID.randomUUID().toString());
        newTask.setTitle(request.getTitle());
        newTask.setDescription(request.getDescription());
        newTask.setDue_date(request.getDue_date());
        newTask.setStatus(request.getStatus() != null ? Task.Status.valueOf(request.getStatus().toUpperCase().replace('-', '_')) : Task.Status.NOT_STARTED);
        newTask.setPriority(request.getPriority() != null ? Task.Priority.valueOf(request.getPriority().toUpperCase()) : Task.Priority.LOW);
        // Atribuir vínculos corretos
        newTask.setEquip_uuid(project.getTeamUuid());
        newTask.setProjectUuid(request.getProject_uuid());
        if (request.getResponsible() != null) {
            var r = request.getResponsible();
            newTask.setResponsible(new User.UserRef(r.getUuid(), r.getName(), r.getUrl_img()));
        } else {
            newTask.setResponsible(null);
        }

        validateResponsible(newTask, team);
        Task savedTask = taskRepository.save(newTask);

        // Adicionar referência da tarefa ao projeto
        if (project.getTasks() == null) project.setTasks(new java.util.ArrayList<>());
        project.getTasks().add(savedTask.toProjectRef());
        projectsRepository.save(project);

        // No longer embed TaskInfo in Equipe; Teams/Projects will maintain references via lists if needed in future
        if (savedTask.getResponsible() != null && savedTask.getResponsible().uuid() != null) {
            addTaskToUser(savedTask, savedTask.getResponsible().uuid());
        }

        return savedTask;
    }

    private void validateResponsible(Task task, Teams team) {
        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            String responsibleUuid = task.getResponsible().uuid();
            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

        boolean isMember = team.getMembers().stream()
            .anyMatch(membro -> membro.uuid().equals(responsibleUuid));
            if (!isMember) {
                throw new InvalidResponsibleException("O usuário responsável não é membro da equipe.");
            }
        }
    }

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) user.setTasks(new ArrayList<>());
            // Persist as Task.TaskUser in the User entity
            Task.TaskUser taskUser = task.toUserRef();
            user.getTasks().add(taskUser);
            userRepository.save(user);
        });
    }
}