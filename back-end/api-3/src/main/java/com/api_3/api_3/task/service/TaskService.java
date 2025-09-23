package com.api_3.api_3.task.service;

import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.equipe.model.ResponsavelTask;
import com.api_3.api_3.equipe.model.TaskInfo;
import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.task.model.Task;
import com.api_3.api_3.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Transactional
    public Task createTask(Task newTask) {
        newTask.setUuid(UUID.randomUUID().toString());
        Task savedTask = taskRepository.save(newTask);

        if (savedTask.getEquip_uuid() != null && !savedTask.getEquip_uuid().isEmpty()) {
            equipeRepository.findById(savedTask.getEquip_uuid()).ifPresent(equipe -> {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setUuid(savedTask.getUuid());
                taskInfo.setTitle(savedTask.getTitle());
                if (savedTask.getDue_date() != null) {
                    taskInfo.setDue_date(savedTask.getDue_date().toString());
                }
                taskInfo.setStatus(savedTask.getStatus());
                taskInfo.setPrioridade(savedTask.getPriority());
                taskInfo.setEquipe_uuid(savedTask.getEquip_uuid());

                if (savedTask.getResponsible() != null) {
                    ResponsavelTask responsavelTask = new ResponsavelTask();
                    responsavelTask.setUuid(savedTask.getResponsible().getUuid());
                    responsavelTask.setName(savedTask.getResponsible().getName());
                    responsavelTask.setImg(savedTask.getResponsible().getUrl_img());
                    taskInfo.setResponsavel(responsavelTask);
                }

                equipe.getTasks().add(taskInfo);
                equipeRepository.save(equipe);
            });
        }

        return savedTask;
    }
}