package com.api_3.api_3.model.entity;
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
 
    private List<String> equipeIds = new ArrayList<>();
    private List<Object> tasks = new ArrayList<>();
    private List<Object> notificacao = new ArrayList<>();
 
    public User(String name, String email , String password , String img){
        this.name = name;
        this.email = email;
        this.password = password;
        this.img = img;
    }
    
    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<String> getEquipeIds() {
        return equipeIds;
    }

    public void setEquipeIds(List<String> equipeIds) {
        this.equipeIds = equipeIds;
    }

    public List<Object> getTasks() {
        return tasks;
    }

    public void setTasks(List<Object> tasks) {
        this.tasks = tasks;
    }

    public List<Object> getNotificacao() {
        return notificacao;
    }

    public void setNotificacao(List<Object> notificacao) {
        this.notificacao = notificacao;
    }
}