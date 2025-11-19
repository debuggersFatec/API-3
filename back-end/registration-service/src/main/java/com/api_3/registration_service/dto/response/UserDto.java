package com.api_3.registration_service.dto.response;

import lombok.Data;

@Data
public class UserDto {
    private String uuid;
    private String name;
    private String email;
    private String img;
    
    public UserDto(String uuid, String name, String email, String img) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.img = img;
    }
}