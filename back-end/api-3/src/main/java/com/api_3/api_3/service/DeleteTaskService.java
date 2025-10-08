package com.api_3.api_3.service;

import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.model.embedded.TaskInfo;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class DeleteTaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Task execute(String uuid) {
        Task task = taskRepository.findById(uuid)
            .orElseThrow(() -> new TaskNotFoundException("Falha ao apagar a tarefa com o ID: " + uuid));

        moveTaskToLixeiraInEquipe(task);
        if (task.getResponsible() != null && task.getResponsible().getUuid() != null) {
            removeTaskFromUser(uuid, task.getResponsible().getUuid());
        }

        task.setStatus("excluida");
        return taskRepository.save(task);
    }

    private void moveTaskToLixeiraInEquipe(Task task) {
        if (task.getEquip_uuid() == null) return;
        equipeRepository.findById(task.getEquip_uuid()).ifPresent(equipe -> {
            Optional<TaskInfo> taskInfoOptional = equipe.getTasks().stream()
                    .filter(ti -> ti.getUuid().equals(task.getUuid()))
                    .findFirst();

            if (taskInfoOptional.isPresent()) {
                TaskInfo taskInfo = taskInfoOptional.get();
                equipe.getTasks().remove(taskInfo);
                if (equipe.getLixeira() == null) {
                    equipe.setLixeira(new ArrayList<>());
                }
                taskInfo.setStatus("excluida");
                equipe.getLixeira().add(taskInfo);
                equipeRepository.save(equipe);
            }
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
}