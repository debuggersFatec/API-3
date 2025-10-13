package com.api_3.api_3.dto.response;

import java.util.List;

public class ProjectResponse {
    private String uuid;
    private String name;
    private boolean active;
    private String teamUuid;
    private List<MemberSummary> members;

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
}
