package com.api_3.api_3.equipe.model;
 
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.api_3.api_3.task.model.Task;
import com.api_3.api_3.user.model.User;

import lombok.Data;
import lombok.NoArgsConstructor;
 
@Document(collection = "equipes")
@Data
@NoArgsConstructor
public class Equipe {
    private String uuid;
    private String name;
    private List<User> membros;
    private List<Task> tasks;
}