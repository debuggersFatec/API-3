package com.api_3.registration_service.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // Teams and tasks mirrors from the core API so legacy mappers continue to work
    private List<Teams.TeamRef> teams = new ArrayList<>();
    private List<Task.TaskUser> tasks = new ArrayList<>();

    public User(String name, String email , String password , String img){
        this.name = name;
        this.email = email;
        this.password = password;
        this.img = img;
    }

    public record UserRef(String uuid, String name, String img) {
        public UserRef(String uuid, String name) { this(uuid, name, null); }
        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public String getImg() { return img; }
    }

    public UserRef toRef() { return new UserRef(this.uuid, this.name, this.img); }

    // Legacy compatibility for getEquipeIds/setEquipeIds used by the old AuthService
    public List<String> getEquipeIds() {
        if (this.teams == null) return List.of();
        return this.teams.stream()
                .map(Teams.TeamRef::uuid)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());
    }

    public void setEquipeIds(List<String> ids) {
        if (ids == null) {
            this.teams = new ArrayList<>();
            return;
        }
        this.teams = ids.stream()
                .filter(id -> id != null && !id.isBlank())
                .map(id -> new Teams.TeamRef(id, null))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}