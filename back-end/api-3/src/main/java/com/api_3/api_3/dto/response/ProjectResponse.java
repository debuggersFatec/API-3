package com.api_3.api_3.dto.response;

import java.util.List;

public class ProjectResponse {
    private String uuid;
    private String name;
    private boolean active;
    private String teamUuid;
    private List<MemberSummary> members;
    private List<TaskSummary> tasks;
    private List<TaskSummary> trashcan;

    public static class MemberSummary {
        private String uuid;
        private String name;
        private String img;

        public MemberSummary() {}
        public MemberSummary(String uuid, String name, String img) {
            this.uuid = uuid; this.name = name; this.img = img;
        }
        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImg() { return img; }
        public void setImg(String img) { this.img = img; }
    }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getTeamUuid() { return teamUuid; }
    public void setTeamUuid(String teamUuid) { this.teamUuid = teamUuid; }
    public List<MemberSummary> getMembers() { return members; }
    public void setMembers(List<MemberSummary> members) { this.members = members; }

    public List<TaskSummary> getTasks() { return tasks; }
    public void setTasks(List<TaskSummary> tasks) { this.tasks = tasks; }
    public List<TaskSummary> getTrashcan() { return trashcan; }
    public void setTrashcan(List<TaskSummary> trashcan) { this.trashcan = trashcan; }

    public static class ResponsibleSummary {
        private String uuid;
        private String name;
        private String img;

        public ResponsibleSummary() {}
        public ResponsibleSummary(String uuid, String name, String img) {
            this.uuid = uuid; this.name = name; this.img = img;
        }
        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getImg() { return img; }
        public void setImg(String img) { this.img = img; }
    }

    public static class TaskSummary {
        private String uuid;
        private String title;
        private java.util.Date due_date;
        private String status;
        private String priority;
        private Boolean is_required_file;
        private String equip_uuid; // team id
        private String project_uuid;
        private ResponsibleSummary responsible;

        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public java.util.Date getDue_date() { return due_date; }
        public void setDue_date(java.util.Date due_date) { this.due_date = due_date; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Boolean getIs_required_file() { return is_required_file; }
        public void setIs_required_file(Boolean is_required_file) { this.is_required_file = is_required_file; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getEquip_uuid() { return equip_uuid; }
        public void setEquip_uuid(String equip_uuid) { this.equip_uuid = equip_uuid; }
        public String getProject_uuid() { return project_uuid; }
        public void setProject_uuid(String project_uuid) { this.project_uuid = project_uuid; }
        public ResponsibleSummary getResponsible() { return responsible; }
        public void setResponsible(ResponsibleSummary responsible) { this.responsible = responsible; }
    }
}
