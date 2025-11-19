package com.api_3.registration_service.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@TypeAlias("com.api_3.api_3.model.entity.User") 
@Data
@NoArgsConstructor
public class User {
    @Id
    private String uuid;
    private String name;
    private String email;
    private String password;
    private String img;

    public User(String name, String email , String password , String img){
        this.name = name;
        this.email = email;
        this.password = password;
        this.img = img;
    }
}