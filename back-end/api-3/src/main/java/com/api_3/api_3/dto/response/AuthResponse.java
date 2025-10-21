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
    private Routes routes;
    private UserInfo user;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private String uuid;
        private String name;
        private String email;
        private String img;
        private List<TeamInfo> teams;
        private List<ProjectInfo> projects;
        private List<TaskInfo> tasks;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamInfo {
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
        private String team_uuid;
        private String project_uuid;
        private java.util.Date due_date;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectInfo {
        private String uuid;
        private String name;
        private boolean active;
        private String team_uuid;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Routes {
        private String teams;     // e.g., /api/teams
        private String projects;  // e.g., /api/projects
        private String members;   // e.g., /api/teams/{teamUuid}/members
        private String tasks;     // e.g., /api/tasks
    }
}