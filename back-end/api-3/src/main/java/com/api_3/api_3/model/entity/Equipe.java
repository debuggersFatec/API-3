package com.api_3.api_3.model.entity;
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.api_3.api_3.model.embedded.TaskInfo;

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
    private List<TaskInfo> lixeira = new ArrayList<>();
}