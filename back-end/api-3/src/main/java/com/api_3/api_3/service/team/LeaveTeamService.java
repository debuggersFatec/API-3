package com.api_3.api_3.service.team;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.exception.TeamNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.NotificationService;
import com.api_3.api_3.service.task.TaskMaintenanceService;

@Service
public class LeaveTeamService {

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private TaskMaintenanceService taskMaintenanceService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void execute(String teamUuid, String userUuid) {
        Teams team = teamsRepository.findById(teamUuid)
                .orElseThrow(() -> new TeamNotFoundException("Team não encontrado com o ID: " + teamUuid));

        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o ID: " + userUuid));

        boolean isMember = team.getMembers().stream().anyMatch(m -> m.getUuid().equals(userUuid));
        if (!isMember) {
            throw new SecurityException("Acesso negado. O utilizador não é membro desta equipa.");
        }

        team.getMembers().removeIf(member -> member.getUuid().equals(userUuid));
        teamsRepository.save(team);

        user.getTeams().removeIf(teamRef -> teamRef.getUuid().equals(teamUuid));
        userRepository.save(user);

        List<Projects> projects = projectsRepository.findByTeamUuid(teamUuid);
        for (Projects project : projects) {
            project.getMembers().removeIf(member -> member.getUuid().equals(userUuid));
            projectsRepository.save(project);
        }

        taskMaintenanceService.unassignForTeam(teamUuid, userUuid);

        if (team.getMembers() == null || team.getMembers().isEmpty()) {
            teamsRepository.deleteById(teamUuid);
        } else {
            notificationService.notifyTeamMemberLeft(teamUuid, userUuid);
        }
    }
}
