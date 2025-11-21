package com.api_3.api_3.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.api_3.api_3.model.embedded.FileAttachment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
public class Task {

    @Id
    private String uuid;
    private String title;
    private String description;
    private Date dueDate;
    private Status status;
    private Priority priority;
    private String fileUrl;
    private Boolean isRequiredFile;
    private List<FileAttachment> requiredFile = new ArrayList<>();
    private String teamUuid;
    private String projectUuid;
    private User.UserRef responsible;
    private List<TaskComment> comments = new ArrayList<>();

    public Task(String title, Status status, Priority priority, String teamUuid, String projectUuid , Boolean isRequiredFile) {
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.teamUuid = teamUuid;
        this.projectUuid = projectUuid;
        this.isRequiredFile = isRequiredFile;
    }

    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        DELETED
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public record TaskUser(
        String uuid,
        String title,
        Date dueDate,
        Status status,
        Priority priority,
        String teamUuid,
        String projectUuid,
        User.UserRef responsible
    ) {
        // Compatibility getters for legacy code
        public String getUuid() { return uuid; }
        public String getTitle() { return title; }
        public Date getDueDate() { return dueDate; }
        public Status getStatus() { return status; }
        public Priority getPriority() { return priority; }
        public String getTeamUuid() { return teamUuid; }
        public String getProjectUuid() { return projectUuid; }
        public User.UserRef getResponsible() { return responsible; }
    }

    public record TaskProject(
        String uuid,
        String title,
        Date dueDate,
        Status status,
        Priority priority,
        String teamUuid,
        String projectUuid,
        User.UserRef responsible
    ) {
        // Compatibility getters for legacy code
        public String getUuid() { return uuid; }
        public String getTitle() { return title; }
        public Date getDueDate() { return dueDate; }
        public Status getStatus() { return status; }
        public Priority getPriority() { return priority; }
        public String getTeamUuid() { return teamUuid; }
        public String getProjectUuid() { return projectUuid; }
        public User.UserRef getResponsible() { return responsible; }
    }
    
    public TaskUser toUserRef() {
        return new TaskUser(this.uuid, this.title, this.dueDate, this.status, this.priority, this.teamUuid, this.projectUuid, this.responsible);
    }

    public TaskProject toProjectRef() {
        return new TaskProject(this.uuid, this.title, this.dueDate, this.status, this.priority, this.teamUuid, this.projectUuid, this.responsible);
    }

    // Legacy compatibility methods for underscore naming convention
    public Date getDue_date() {
        return this.dueDate;
    }
    
    public void setDue_date(Date dueDate) {
        this.dueDate = dueDate;
    }
    
    public String getEquip_uuid() {
        return this.teamUuid;
    }
    
    public void setEquip_uuid(String teamUuid) {
        this.teamUuid = teamUuid;
    }
}
