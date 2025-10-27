package com.api_3.api_3.service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.dto.request.UpdateTaskRequest;
import com.api_3.api_3.exception.InvalidResponsibleException;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TaskNotFoundException;
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
public class UpdateTaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectsRepository projectsRepository;

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
        if (request.getIsRequiredFile() != null) {
            existingTask.setIsRequiredFile(request.getIsRequiredFile());
        }
        // Atualiza o responsável
        if (request.getResponsible() != null) {
            var r = request.getResponsible();
            User.UserRef newResponsibleRef = new User.UserRef(r.getUuid(), r.getName(), r.getUrl_img());
            existingTask.setResponsible(newResponsibleRef);
        } else {
            existingTask.setResponsible(null);
        }

        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
             validateResponsible(existingTask);
        }
        Task savedTask = taskRepository.save(existingTask);

        if (savedTask.getProjectUuid() != null) {
            projectsRepository.findById(savedTask.getProjectUuid()).ifPresent(project -> {
                boolean changed = false;
                if (project.getTasks() != null) {
                    // Tenta encontrar a referência antiga para remover
                    Optional<Task.TaskProject> oldRefOpt = project.getTasks().stream()
                        .filter(tp -> tp != null && tp.uuid().equals(savedTask.getUuid()))
                        .findFirst();

                    if (oldRefOpt.isPresent()) {
                         Task.TaskProject newRef = savedTask.toProjectRef();
                         if (!oldRefOpt.get().equals(newRef)) {
                             project.getTasks().remove(oldRefOpt.get());
                             project.getTasks().add(newRef);
                             changed = true;
                         }
                    } else {
                        project.getTasks().add(savedTask.toProjectRef());
                        changed = true;
                    }
                } else {
                     project.setTasks(new ArrayList<>());
                     project.getTasks().add(savedTask.toProjectRef());
                     changed = true;
                }

                if (changed) {
                    projectsRepository.save(project); // Salva o projeto SOMENTE se houve mudança
                }
            });
        }


        manageUserTaskAssignment(savedTask, oldResponsibleUuid, newResponsibleUuid);

        return savedTask;
    }

    private void validateResponsible(Task task) {
        Teams team = teamsRepository.findById(task.getEquip_uuid())
                .orElseThrow(() -> new TeamNotFoundException("Team com ID " + task.getEquip_uuid() + " não encontrado para a tarefa " + task.getUuid()));

        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            String responsibleUuid = task.getResponsible().uuid();
            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

            if (task.getProjectUuid() == null) {
                throw new InvalidResponsibleException("Tarefa não está vinculada a um projeto válido.");
            }
            Projects project = projectsRepository.findById(task.getProjectUuid())
                .orElseThrow(() -> new ProjectNotFoundException("Projeto com ID " + task.getProjectUuid() + " não encontrado."));

            boolean isProjectMember = project.getMembers() != null && project.getMembers().stream()
                .anyMatch(m -> m != null && responsibleUuid.equals(m.getUuid())); // Adicionado null check para m
            if (!isProjectMember) {
                throw new InvalidResponsibleException("O usuário responsável com ID " + responsibleUuid + " não é membro do projeto '" + project.getName() + "'.");
            }

             boolean isTeamMember = team.getMembers() != null && team.getMembers().stream()
                .anyMatch(membro -> membro != null && membro.uuid().equals(responsibleUuid));
             if (!isTeamMember) {
                throw new InvalidResponsibleException("O usuário responsável com ID " + responsibleUuid + " não é membro da equipe '" + team.getName() + "' (Inconsistência encontrada).");
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

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) {
                user.setTasks(new ArrayList<>());
            }
            user.getTasks().removeIf(t -> t != null && task.getUuid().equals(t.uuid()));
            Task.TaskUser taskUser = task.toUserRef();
            user.getTasks().add(taskUser);
            userRepository.save(user);
        });
    }

    private void removeTaskFromUser(String taskUuid, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                boolean removed = user.getTasks().removeIf(t -> t != null && taskUuid.equals(t.uuid())); 
                if (removed) {
                    userRepository.save(user);
                }
            }
        });
    }

    private void updateTaskInUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) return;
            int index = -1;
            for (int i = 0; i < user.getTasks().size(); i++) {
                 Task.TaskUser tu = user.getTasks().get(i);
                 if (tu != null && task.getUuid().equals(tu.uuid())) { 
                    index = i;
                    break;
                 }
            }

            if (index != -1) {
                user.getTasks().remove(index);
                user.getTasks().add(task.toUserRef()); 
                userRepository.save(user);
            }
        });
    }
}