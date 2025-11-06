package com.api_3.api_3.service.project;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.dto.request.CreateProjectRequest; 
import com.api_3.api_3.dto.request.UpdateProjectRequest;
import com.api_3.api_3.dto.response.ProjectResponse;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TeamNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.ProjectMapper;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.NotificationService;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectMapper projectMapper; 
    @Autowired private NotificationService notificationService;
    @Override
    public List<ProjectResponse> listProjectsByTeam(String teamUuid, String userEmail) {
        assertMemberOfTeam(teamUuid, userEmail); 
        List<Projects> projects = projectsRepository.findByTeamUuid(teamUuid);
        return projectMapper.toProjectResponseList(projects); 
    }

    @Override
    public ProjectResponse getProjectDetails(String projectUuid, String userEmail) {
        assertMemberOfProject(projectUuid, userEmail); 
        Projects project = findProjectByIdOrThrow(projectUuid);
        return projectMapper.toProjectResponse(project); 
    }

    @Override
    @Transactional
    public ProjectResponse createProject(String teamUuid, CreateProjectRequest request, String userEmail) {
        assertMemberOfTeam(teamUuid, userEmail);

        Teams team = findTeamByIdOrThrow(teamUuid);
        User creator = findUserByEmailOrThrow(userEmail);

        Projects p = new Projects();
        p.setUuid(UUID.randomUUID().toString());
        p.setName(request.getName());
        p.setActive(true);
        p.setTeamUuid(teamUuid);

        if (p.getMembers() == null) {
            p.setMembers(new java.util.ArrayList<>());
        }
        p.getMembers().add(creator.toRef()); 

        Projects savedProject = projectsRepository.save(p);

        if (team.getProjects() == null) {
            team.setProjects(new java.util.ArrayList<>());
        }
        team.getProjects().add(savedProject.toRef());
        teamsRepository.save(team);
        creator.getTeams().forEach(teamRef -> {
            if (teamRef.getUuid().equals(teamUuid)) {
                teamRef.getProjects().add(savedProject.toRef());
            }
        });
        userRepository.save(creator);
                

        return projectMapper.toProjectResponse(savedProject);
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(String projectUuid, UpdateProjectRequest request, String userEmail) {
        assertMemberOfProject(projectUuid, userEmail); 

        Projects p = findProjectByIdOrThrow(projectUuid);


        p.setName(request.getName());
        if (request.getActive() != null) {
            p.setActive(request.getActive());
        }
        Projects savedProject = projectsRepository.save(p);

      
        syncProjectRefOnTeam(savedProject);

        return projectMapper.toProjectResponse(savedProject); 
    }

    @Override
    @Transactional
    public ProjectResponse activateProject(String projectUuid, String userEmail) {
        assertMemberOfProject(projectUuid, userEmail);
        Projects project = findProjectByIdOrThrow(projectUuid);
        project.getMembers().forEach(memberRef -> {
            User user = findUserByIdOrThrow(memberRef.getUuid());
            user.getTeams().forEach(teamRef -> {
                for (int i = 0; i < teamRef.getProjects().size(); i++) {
                    Projects.ProjectRef projectRef = teamRef.getProjects().get(i);
                    if (projectRef.getUuid().equals(projectUuid)) {
                        Projects updatedProjectRef = new Projects();
                        updatedProjectRef.setUuid(projectRef.getUuid());
                        updatedProjectRef.setName(project.getName());
                        updatedProjectRef.setActive(true);
                        teamRef.getProjects().set(i, updatedProjectRef.toRef());
                        break;
                    }
                }
            });
            userRepository.save(user);
        });
        Projects p = findProjectByIdOrThrow(projectUuid);
        return updateProjectStatus(p, true); 
    }

    @Override
    @Transactional
    public ProjectResponse addMemberToProject(String projectUuid, String userUuidToAdd, String userEmail) {
        
        assertMemberOfProject(projectUuid, userEmail); 

        Projects p = findProjectByIdOrThrow(projectUuid);
        User userToAdd = findUserByIdOrThrow(userUuidToAdd);
        Teams team = findTeamByIdOrThrow(p.getTeamUuid());

        boolean isTeamMember = team.getMembers().stream()
                                   .anyMatch(m -> userUuidToAdd.equals(m.getUuid()));
        if (!isTeamMember) {
            throw new SecurityException("Usuário não é membro da equipe do projeto.");
        }

      
        boolean alreadyMember = p.getMembers().stream()
                                  .anyMatch(m -> userUuidToAdd.equals(m.getUuid()));
        if (!alreadyMember) {
            if (p.getMembers() == null) {
                p.setMembers(new java.util.ArrayList<>());
            }
            p.getMembers().add(userToAdd.toRef());
            projectsRepository.save(p);
            userToAdd.getTeams().forEach(teamRef -> {
                if (teamRef.getUuid().equals(team.getUuid())) {
                    teamRef.getProjects().forEach(projectRef -> {
                        if (!projectRef.getUuid().equals(projectUuid)) {
                            Projects userProjects = new Projects();
                            userProjects.setUuid(projectUuid);
                            userProjects.setName(p.getName());
                            userProjects.setActive(p.isActive());
                            teamRef.getProjects().add(userProjects.toRef());
                        }
                    });
                }
            });
            userRepository.save(userToAdd);

            notificationService.notifyProjectMemberJoined(projectUuid, userUuidToAdd);
        }

        return projectMapper.toProjectResponse(p); 
    }

    
    private Projects findProjectByIdOrThrow(String projectUuid) {
        return projectsRepository.findById(projectUuid)
                .orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado com o ID: " + projectUuid));
    }

     private Teams findTeamByIdOrThrow(String teamUuid) {
        return teamsRepository.findById(teamUuid)
                .orElseThrow(() -> new TeamNotFoundException("Team não encontrado com o ID: " + teamUuid));
    }

     private User findUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o email: " + email));
    }
     private User findUserByIdOrThrow(String uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o ID: " + uuid));
    }


    private void assertMemberOfTeam(String teamUuid, String email) {
        Teams team = findTeamByIdOrThrow(teamUuid);
        User user = findUserByEmailOrThrow(email);
        boolean isMember = team.getMembers().stream()
                               .anyMatch(m -> user.getUuid().equals(m.getUuid()));
        if (!isMember) {
            throw new SecurityException("Acesso negado à equipe " + team.getName());
        }
    }

    
    private void assertMemberOfProject(String projectUuid, String email) {
        Projects p = findProjectByIdOrThrow(projectUuid);
       
        assertMemberOfTeam(p.getTeamUuid(), email);
    }

    
    private ProjectResponse updateProjectStatus(Projects p, boolean newStatus) {
        p.setActive(newStatus);
        Projects savedProject = projectsRepository.save(p);
        syncProjectRefOnTeam(savedProject);
        return projectMapper.toProjectResponse(savedProject);
    }

    private void syncProjectRefOnTeam(Projects project) {
        Teams team = teamsRepository.findById(project.getTeamUuid()).orElse(null);
        if (team != null && team.getProjects() != null) {
            List<Projects.ProjectRef> updatedProjectRefs = team.getProjects().stream()
                .map(pr -> pr.getUuid().equals(project.getUuid()) ? project.toRef() : pr)
                .collect(Collectors.toList());
            team.setProjects(updatedProjectRefs);
            teamsRepository.save(team);
        }
    }
}
