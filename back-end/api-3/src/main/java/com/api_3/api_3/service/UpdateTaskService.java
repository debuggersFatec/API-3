package com.api_3.api_3.service;

import com.api_3.api_3.dto.request.UpdateTaskRequest;
import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.InvalidResponsibleException;
import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.model.embedded.ResponsavelTask;
import com.api_3.api_3.model.embedded.TaskInfo;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class UpdateTaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Task execute(String uuid, UpdateTaskRequest request) {
        Task existingTask = taskRepository.findById(uuid)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada para atualizar com o ID: " + uuid));

    String oldResponsibleUuid = (existingTask.getResponsible() != null) ? existingTask.getResponsible().uuid() : null;
    String newResponsibleUuid = (request.getResponsible() != null) ? request.getResponsible().getUuid() : null;

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
    existingTask.setDue_date(request.getDue_date());
    existingTask.setStatus(request.getStatus() != null ? Task.Status.valueOf(request.getStatus().toUpperCase().replace('-', '_')) : existingTask.getStatus());
        existingTask.setPriority(request.getPriority() != null ? Task.Priority.valueOf(request.getPriority().toUpperCase()) : existingTask.getPriority());
        if (request.getResponsible() != null) {
            var r = request.getResponsible();
            existingTask.setResponsible(new User.UserRef(r.getUuid(), r.getName(), r.getUrl_img()));
        } else {
            existingTask.setResponsible(null);
        }

        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
            validateResponsible(existingTask);
        }
        Task savedTask = taskRepository.save(existingTask);

        // A lógica agora é chamada através de métodos privados nesta mesma classe
        updateTaskInEquipe(savedTask);
        manageUserTaskAssignment(savedTask, oldResponsibleUuid, newResponsibleUuid);

        return savedTask;
    }

    private void validateResponsible(Task task) {
    Teams team = teamsRepository.findById(task.getEquip_uuid())
                .orElseThrow(() -> new EquipeNotFoundException("Equipe (team) com ID " + task.getEquip_uuid() + " não encontrada."));

    if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
        String responsibleUuid = task.getResponsible().uuid();
            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

            boolean isMember = team.getMembers().stream()
                    .anyMatch(membro -> membro.uuid().equals(responsibleUuid));
            if (!isMember) {
                throw new InvalidResponsibleException("O usuário responsável com ID " + responsibleUuid + " não é membro da equipe " + team.getName() + ".");
            }
        }
    }

    private void manageUserTaskAssignment(Task task, String oldResponsibleUuid, String newResponsibleUuid) {
        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
            if (oldResponsibleUuid != null) {
                removeTaskFromUser(task.getUuid(), oldResponsibleUuid);
            }
            if (newResponsibleUuid != null) {
                addTaskToUser(task, newResponsibleUuid);
            }
        } else if (newResponsibleUuid != null) {
            updateTaskInUser(task, newResponsibleUuid);
        }
    }

    private void updateTaskInEquipe(Task task) {
        // For new architecture, we are not maintaining TaskInfo inside Teams; consider updating a Project/Team view in future
    }

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) user.setTasks(new ArrayList<>());
            Task.TaskUser taskUser = task.toUserRef();
            user.getTasks().add(taskUser);
            userRepository.save(user);
        });
    }

    private void removeTaskFromUser(String taskUuid, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                boolean removed = user.getTasks().removeIf(t -> t instanceof Task.TaskUser && ((Task.TaskUser) t).uuid().equals(taskUuid));
                if (removed) {
                    userRepository.save(user);
                }
            }
        });
    }

    private void updateTaskInUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) return;
            user.getTasks().stream()
                    .filter(t -> t instanceof Task.TaskUser && ((Task.TaskUser) t).uuid().equals(task.getUuid()))
                    .findFirst()
                    .ifPresent(tu -> {
                        // Replace the record by removing and adding a new one
                        user.getTasks().remove(tu);
                        user.getTasks().add(task.toUserRef());
                        userRepository.save(user);
                    });
        });
    }
}