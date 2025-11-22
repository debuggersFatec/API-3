package com.api_3.api_3.mapper;

import java.util.Collections;
import java.util.List;
import java.util.function.Function; 
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.api_3.api_3.dto.response.ProjectResponse;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task; 

@Component 
public class ProjectMapper {

    
    public ProjectResponse toProjectResponse(Projects p) {
        if (p == null) {
            return null;
        }
        ProjectResponse dto = new ProjectResponse();
        dto.setUuid(p.getUuid());
        dto.setName(p.getName());
        dto.setActive(p.isActive());
        dto.setTeamUuid(p.getTeamUuid());

     
        dto.setMembers(
                (p.getMembers() == null) ? Collections.emptyList() :
                p.getMembers().stream()
                 .map(m -> new ProjectResponse.MemberSummary(m.getUuid(), m.getName(), m.getImg()))
                 .collect(Collectors.toList())
        );

        
        Function<Task.TaskProject, ProjectResponse.TaskSummary> mapTask = tp -> {
            if (tp == null) return null;
            ProjectResponse.TaskSummary ts = new ProjectResponse.TaskSummary();
            ts.setUuid(tp.getUuid());
            ts.setTitle(tp.getTitle());
            ts.setDue_date(tp.getDueDate());
            ts.setStatus(tp.getStatus() != null ? tp.getStatus().name() : null);
            ts.setIs_required_file(tp.getIsRequiredFile());
            ts.setPriority(tp.getPriority() != null ? tp.getPriority().name() : null);
            ts.setEquip_uuid(tp.getTeamUuid()); // Mantendo nome antigo por compatibilidade DTO
            ts.setProject_uuid(tp.getProjectUuid());
            if (tp.getResponsible() != null) {
                ProjectResponse.ResponsibleSummary rs = new ProjectResponse.ResponsibleSummary(
                        tp.getResponsible().getUuid(),
                        tp.getResponsible().getName(),
                        tp.getResponsible().getImg()
                );
                ts.setResponsible(rs);
            }
            return ts;
        };

        dto.setTasks(
            (p.getTasks() == null) ? Collections.emptyList() :
            p.getTasks().stream().map(mapTask).collect(Collectors.toList())
        );
        dto.setTrashcan(
            (p.getTrashcan() == null) ? Collections.emptyList() :
            p.getTrashcan().stream().map(mapTask).collect(Collectors.toList())
        );

        return dto;
    }

     public List<ProjectResponse> toProjectResponseList(List<Projects> projects) {
        if (projects == null) {
            return Collections.emptyList();
        }
        return projects.stream()
                       .map(this::toProjectResponse)
                       .collect(Collectors.toList());
    }
}