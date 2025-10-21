package com.api_3.api_3.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "projects")
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

    public record ProjectRef(String uuid, String name, boolean isActive) {
        public ProjectRef(String uuid, String name) {
            this(uuid, name, true);
        }

        // Compatibility getters for legacy code
        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public boolean getIsActive() { return isActive; }
        public boolean isActive() { return isActive; }
    }

    public ProjectRef toRef() {
        return new ProjectRef(this.uuid, this.name, this.isActive);
    }
    
}
