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