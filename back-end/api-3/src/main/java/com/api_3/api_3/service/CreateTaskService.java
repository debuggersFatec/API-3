package com.api_3.api_3.service;

import com.api_3.api_3.dto.request.CreateTaskRequest;
import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.InvalidResponsibleException;
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
import java.util.UUID;

@Service
public class CreateTaskService {
    @Autowired private TaskRepository taskRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Task execute(CreateTaskRequest request) {
        Equipe equipe = equipeRepository.findById(request.getEquip_uuid())
                .orElseThrow(() -> new EquipeNotFoundException("Equipe com ID " + request.getEquip_uuid() + " não encontrada."));

        Task newTask = new Task();
        newTask.setUuid(UUID.randomUUID().toString());
        newTask.setTitle(request.getTitle());
        newTask.setDescription(request.getDescription());
        newTask.setDue_date(request.getDue_date());
        newTask.setStatus(request.getStatus() != null ? request.getStatus() : "not-started");
        newTask.setPriority(request.getPriority());
        newTask.setEquip_uuid(request.getEquip_uuid());
        newTask.setResponsible(request.getResponsible());

        validateResponsible(newTask, equipe);
        Task savedTask = taskRepository.save(newTask);

        addTaskInfoToEquipe(savedTask, equipe);
        if (savedTask.getResponsible() != null && savedTask.getResponsible().getUuid() != null) {
            addTaskToUser(savedTask, savedTask.getResponsible().getUuid());
        }

        return savedTask;
    }

    private void validateResponsible(Task task, Equipe equipe) {
        if (task.getResponsible() != null && task.getResponsible().getUuid() != null) {
            String responsibleUuid = task.getResponsible().getUuid();
            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

            boolean isMember = equipe.getMembros().stream()
                    .anyMatch(membro -> membro.getUuid().equals(responsibleUuid));
            if (!isMember) {
                throw new InvalidResponsibleException("O usuário responsável não é membro da equipe.");
            }
        }
    }

    private void addTaskInfoToEquipe(Task task, Equipe equipe) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setUuid(task.getUuid());
        taskInfo.setTitle(task.getTitle());
        taskInfo.setStatus(task.getStatus());
        taskInfo.setPrioridade(task.getPriority());
        taskInfo.setEquipe_uuid(task.getEquip_uuid());
        taskInfo.setDue_date(task.getDue_date() != null ? task.getDue_date().toString() : null);

        if (task.getResponsible() != null) {
            ResponsavelTask responsavelTask = new ResponsavelTask();
            responsavelTask.setUuid(task.getResponsible().getUuid());
            responsavelTask.setName(task.getResponsible().getName());
            responsavelTask.setImg(task.getResponsible().getUrl_img());
            taskInfo.setResponsavel(responsavelTask);
        }

        if (equipe.getTasks() == null) {
            equipe.setTasks(new ArrayList<>());
        }
        equipe.getTasks().add(taskInfo);
        equipeRepository.save(equipe);
    }

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) user.setTasks(new ArrayList<>());

            TaskInfo taskInfoForUser = new TaskInfo();
            taskInfoForUser.setUuid(task.getUuid());
            taskInfoForUser.setTitle(task.getTitle());
            taskInfoForUser.setStatus(task.getStatus());
            taskInfoForUser.setPrioridade(task.getPriority());
            taskInfoForUser.setEquipe_uuid(task.getEquip_uuid());
            taskInfoForUser.setDue_date(task.getDue_date() != null ? task.getDue_date().toString() : null);

            user.getTasks().add(taskInfoForUser);
            userRepository.save(user);
        });
    }
}