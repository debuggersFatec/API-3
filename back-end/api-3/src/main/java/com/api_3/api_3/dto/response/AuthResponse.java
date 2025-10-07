package com.api_3.api_3.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserInfo user;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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
    @NoArgsConstructor
    public static class EquipeInfo {
        private String uuid;
        private String name;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskInfo {
        private String uuid;
        private String title;
        private String status;
        private String priority;
        private String equip_uuid;
        private java.util.Date due_date;
    }
}