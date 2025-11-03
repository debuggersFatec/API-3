package com.api_3.api_3.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.api_3.api_3.dto.response.TaskResponse;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Responsible;

@Component
public class TaskMapper {

    @Autowired
    private CommentMapper commentMapper; 

    public TaskResponse toTaskResponse(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponse dto = new TaskResponse();
        dto.setUuid(task.getUuid());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        
        dto.setDue_date(task.getDueDate()); 

        dto.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        dto.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        dto.setTeam_uuid(task.getEquip_uuid());
        dto.setProject_uuid(task.getProjectUuid());
        dto.setRequiredFile(task.getRequiredFile());
        dto.setIsRequiredFile(task.getIsRequiredFile());
        
        if (task.getResponsible() != null) {
          
            Responsible resp = new Responsible();
             resp.setUuid(task.getResponsible().uuid());
             resp.setName(task.getResponsible().name());
             resp.setUrl_img(task.getResponsible().img());
            dto.setResponsible(resp);
        } else {
            dto.setResponsible(null);
        }

        // Usa o CommentMapper injetado para mapear a lista de comentários
        dto.setComments(commentMapper.toCommentResponseList(task.getComments()));

        return dto;
    }

    public List<TaskResponse> toTaskResponseList(List<Task> tasks) {
        if (tasks == null) {
             return Collections.emptyList();
        }
        return tasks.stream()
                .map(this::toTaskResponse) 
                .collect(Collectors.toList());
    }
}