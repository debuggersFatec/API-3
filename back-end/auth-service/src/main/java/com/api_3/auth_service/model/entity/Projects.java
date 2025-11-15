package com.api_3.auth_service.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "projects")
@TypeAlias("com.api_3.api_3.model.entity.Projects")
@Data
@NoArgsConstructor
public class Projects {
    @Id
    private String uuid;
    private String name;
    private boolean isActive;
    private String teamUuid;

    private List<User.UserRef> members = new ArrayList<>();

    private List<Task.TaskProject> tasks = new ArrayList<>();
    private List<Task.TaskProject> trashcan = new ArrayList<>();

    public Projects(String name, boolean isActive, String teamUuid) {
        this.name = name;
        this.isActive = isActive;
        this.teamUuid = teamUuid;
    }

    public record ProjectRef(String uuid, String name, Boolean isActive) {
        public ProjectRef(String uuid, String name) {
            this(uuid, name, Boolean.TRUE);
        }

        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public boolean getIsActive() { return isActive != null ? isActive : true; }
    }

    public ProjectRef toRef() {
        return new ProjectRef(this.uuid, this.name, this.isActive);
    }
}
