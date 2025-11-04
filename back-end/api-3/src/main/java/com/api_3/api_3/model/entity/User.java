package com.api_3.api_3.model.entity;
 
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.api_3.api_3.model.entity.Task.TaskUser;
import com.api_3.api_3.model.entity.Teams.TeamRef;

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
 
    private List<TeamRef> teams = new ArrayList<>(); 
    private List<TaskUser> tasks = new ArrayList<>();
    private List<Object> notification = new ArrayList<>();
 
    public User(String name, String email , String password , String img){
        this.name = name;
        this.email = email;
        this.password = password;
        this.img = img;
    }
    
    public record UserRef(String uuid, String name, String img) {
        public UserRef(String uuid, String name) {
            this(uuid, name, null);
        }

        // Compatibility getters for legacy code expecting JavaBean-style accessors
        public String getUuid() { return uuid; }
        public String getName() { return name; }
        public String getImg() { return img; }
    }

    public UserRef toRef() {
        return new UserRef(this.uuid, this.name, this.img);
    }

    // Compatibility shims with legacy "Equipe" API -----------------------
    // Many services/controllers still call getEquipeIds()/setEquipeIds().
    // We derive them from the new teams (List<TeamRef>).
    public List<String> getEquipeIds() {
        if (this.teams == null) return List.of();
        return this.teams.stream()
                .map(TeamRef::uuid)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());
    }

    // Accept a list of team IDs and convert to TeamRef with null name
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