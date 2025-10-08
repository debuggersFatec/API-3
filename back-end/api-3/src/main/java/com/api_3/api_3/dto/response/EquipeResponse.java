package com.api_3.api_3.dto.response;

import com.api_3.api_3.model.embedded.TaskInfo;
import lombok.Data;
import java.util.List;

@Data
public class EquipeResponse {
    private String uuid;
    private String name;
    private List<MembroResponse> membros;
    private List<TaskInfo> tasks; // Manteremos tasks por enquanto, antes de adicionar Projetos
}