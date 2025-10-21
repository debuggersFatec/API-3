package com.api_3.api_3.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "teams")
@Data
@NoArgsConstructor
public class Teams {
    @Id
    private String uuid;
    private String name;

    private List<User.UserRef> members = new ArrayList<>();

    private List<Projects.ProjectRef> projects = new ArrayList<>();

    public Teams(String name) {
        this.name = name;
    }

    public record TeamRef(String uuid, String name, List<Projects.ProjectRef> projects) {
        public TeamRef(String uuid, String name) {
            this(uuid, name, new ArrayList<>());
        }

        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public List<Projects.ProjectRef> getProjects() { return projects; }
    }

    public TeamRef toRef() {
        return new TeamRef(this.uuid, this.name, this.projects);

    }
}
