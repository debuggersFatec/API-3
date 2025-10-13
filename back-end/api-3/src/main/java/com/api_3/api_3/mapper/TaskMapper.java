package com.api_3.api_3.mapper;

import com.api_3.api_3.dto.response.TaskResponse;
import com.api_3.api_3.model.entity.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    public TaskResponse toTaskResponse(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponse dto = new TaskResponse();
        dto.setUuid(task.getUuid());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDue_date(task.getDue_date());
        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        dto.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        dto.setEquip_uuid(task.getEquip_uuid());
        if (task.getResponsible() != null) {
            // Map User.UserRef (record) to Responsible DTO-compatible class if needed
            com.api_3.api_3.model.entity.Responsible resp = new com.api_3.api_3.model.entity.Responsible();
            resp.setUuid(task.getResponsible().uuid());
            resp.setName(task.getResponsible().name());
            resp.setUrl_img(task.getResponsible().img());
            dto.setResponsible(resp);
        } else {
            dto.setResponsible(null);
        }

        return dto;
    }

    public List<TaskResponse> toTaskResponseList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }
}