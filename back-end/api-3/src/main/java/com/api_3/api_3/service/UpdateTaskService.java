package com.api_3.api_3.service;

import com.api_3.api_3.dto.request.UpdateTaskRequest;
import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.InvalidResponsibleException;
import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.model.embedded.ResponsavelTask;
import com.api_3.api_3.model.embedded.TaskInfo;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.EquipeRepository;
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
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Task execute(String uuid, UpdateTaskRequest request) {
        Task existingTask = taskRepository.findById(uuid)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada para atualizar com o ID: " + uuid));

        String oldResponsibleUuid = (existingTask.getResponsible() != null) ? existingTask.getResponsible().getUuid() : null;
        String newResponsibleUuid = (request.getResponsible() != null) ? request.getResponsible().getUuid() : null;

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setDue_date(request.getDue_date());
        existingTask.setStatus(request.getStatus());
        existingTask.setPriority(request.getPriority());
        existingTask.setResponsible(request.getResponsible());

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
        Equipe equipe = equipeRepository.findById(task.getEquip_uuid())
                .orElseThrow(() -> new EquipeNotFoundException("Equipe com ID " + task.getEquip_uuid() + " não encontrada."));

        if (task.getResponsible() != null && task.getResponsible().getUuid() != null) {
            String responsibleUuid = task.getResponsible().getUuid();
            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

            boolean isMember = equipe.getMembros().stream()
                    .anyMatch(membro -> membro.getUuid().equals(responsibleUuid));
            if (!isMember) {
                throw new InvalidResponsibleException("O usuário responsável com ID " + responsibleUuid + " não é membro da equipe " + equipe.getName() + ".");
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
        if (task.getEquip_uuid() == null) return;
        equipeRepository.findById(task.getEquip_uuid()).ifPresent(equipe -> {
            equipe.getTasks().stream()
                    .filter(taskInfo -> taskInfo.getUuid().equals(task.getUuid()))
                    .findFirst()
                    .ifPresent(taskInfo -> {
                        taskInfo.setTitle(task.getTitle());
                        taskInfo.setStatus(task.getStatus());
                        taskInfo.setPrioridade(task.getPriority());
                        taskInfo.setDue_date(task.getDue_date() != null ? task.getDue_date().toString() : null);
                        if (task.getResponsible() != null) {
                            ResponsavelTask responsavelTask = new ResponsavelTask();
                            responsavelTask.setUuid(task.getResponsible().getUuid());
                            responsavelTask.setName(task.getResponsible().getName());
                            responsavelTask.setImg(task.getResponsible().getUrl_img());
                            taskInfo.setResponsavel(responsavelTask);
                        } else {
                            taskInfo.setResponsavel(null);
                        }
                        equipeRepository.save(equipe);
                    });
        });
    }

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) user.setTasks(new ArrayList<>());
            TaskInfo taskInfo = createTaskInfoForUser(task);
            user.getTasks().add(taskInfo);
            userRepository.save(user);
        });
    }

    private void removeTaskFromUser(String taskUuid, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                boolean removed = user.getTasks().removeIf(t -> t instanceof TaskInfo && ((TaskInfo) t).getUuid().equals(taskUuid));
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
                    .filter(t -> t instanceof TaskInfo && ((TaskInfo) t).getUuid().equals(task.getUuid()))
                    .map(t -> (TaskInfo) t)
                    .findFirst()
                    .ifPresent(taskInfo -> {
                        taskInfo.setTitle(task.getTitle());
                        taskInfo.setStatus(task.getStatus());
                        taskInfo.setPrioridade(task.getPriority());
                        taskInfo.setDue_date(task.getDue_date() != null ? task.getDue_date().toString() : null);
                        userRepository.save(user);
                    });
        });
    }

    private TaskInfo createTaskInfoForUser(Task task) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setUuid(task.getUuid());
        taskInfo.setTitle(task.getTitle());
        taskInfo.setStatus(task.getStatus());
        taskInfo.setPrioridade(task.getPriority());
        taskInfo.setEquipe_uuid(task.getEquip_uuid());
        taskInfo.setDue_date(task.getDue_date() != null ? task.getDue_date().toString() : null);
        return taskInfo;
    }
}