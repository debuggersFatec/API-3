package com.api_3.api_3.auth;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserInfo user;
    
    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private String uuid;
        private String name;
        private String email;
        private String img;
        private List<EquipeInfo> equipes;
        private List<TaskInfo> tasks;
    }
    
    @Data
    @AllArgsConstructor
    public static class EquipeInfo {
        private String uuid;
        private String name;
    }
    
    @Data
    @AllArgsConstructor
    public static class TaskInfo {
        private String uuid;
        private String title;
        private String status;
        private String priority;
        private String equip_uuid;
    }
}