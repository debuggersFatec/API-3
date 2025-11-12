package com.api_3.api_3.service.notification;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;

@Component
public class NotificationRecipientResolver {

    public Set<String> projectRecipients(Projects project, String actorUuid, boolean includeActor, Set<String> exclude) {
        Set<String> out = new HashSet<>();
        if (project == null || project.getMembers() == null || project.getMembers().isEmpty()) return out;
        for (User.UserRef m : project.getMembers()) {
            if (m == null || m.getUuid() == null) continue;
            if (!includeActor && m.getUuid().equals(actorUuid)) continue;
            if (exclude != null && exclude.contains(m.getUuid())) continue;
            out.add(m.getUuid());
        }
        return out;
    }

    public Set<String> teamRecipients(Teams team, String actorUuid, boolean includeActor, Set<String> exclude) {
        Set<String> out = new HashSet<>();
        if (team == null || team.getMembers() == null || team.getMembers().isEmpty()) return out;
        for (User.UserRef m : team.getMembers()) {
            if (m == null || m.getUuid() == null) continue;
            if (!includeActor && m.getUuid().equals(actorUuid)) continue;
            if (exclude != null && exclude.contains(m.getUuid())) continue;
            out.add(m.getUuid());
        }
        return out;
    }
}
