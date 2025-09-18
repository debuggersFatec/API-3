package com.api_3.api_3.equipe.model;
 
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Document(collection = "equipes")
@Data
@NoArgsConstructor
public class Equipe {
    @Id
    private String uuid;
    private String name;
    private List<Membro> membros;
    private List<TaskInfo> tasks;
}

@Data
@NoArgsConstructor
class Membro {
    private String uuid;
    private String name;
    private String img;
    private int atribuidas_tasks;
    private int concluidas_tasks;
    private int vencidas_tasks;
}

@Data 
@NoArgsConstructor
class TaskInfo {
    private String uuid;
    private String title;
    private String due_date;
    private String status;
    private String prioridade;
    private String equipe_uuid;
    private ResponsavelTask responsavel;
}

@Data
@NoArgsConstructor
class ResponsavelTask {
    private String uuid;
    private String name;
    private String img;
}