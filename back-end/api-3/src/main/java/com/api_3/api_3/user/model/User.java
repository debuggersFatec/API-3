package com.api_3.api_3.user.model;
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.api_3.api_3.equipe.model.Equipe;

import lombok.Data;
import lombok.NoArgsConstructor;
 
@Document(collection = "users")
@Data
@NoArgsConstructor
 
public class User {
    @Id
    private String uuid;
    private String name;
    private String email;
    private String password;
    private String img;
 
    private List<Equipe> equipes = new ArrayList<>();
    private List<Object> tasks = new ArrayList<>();
    private List<Object> lixeira = new ArrayList<>();
    private List<Object> notificacao = new ArrayList<>();
 
    public User(String name, String email , String password , String img){
        this.name = name;
        this.email = email;
        this.password = password;
        this.img = img;
    }
}