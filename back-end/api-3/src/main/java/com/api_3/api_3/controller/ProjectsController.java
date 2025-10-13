package com.api_3.api_3.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.dto.request.CreateProjectRequest;
import com.api_3.api_3.dto.request.UpdateProjectRequest;
import com.api_3.api_3.dto.response.ProjectResponse;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;

    private String currentUserEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private void assertMemberOfTeam(String teamUuid, String email) {
        Teams team = teamsRepository.findById(teamUuid)
                .orElseThrow(() -> new EquipeNotFoundException("Equipa não encontrada com o ID: " + teamUuid));
        Optional<User> u = userRepository.findByEmail(email);
        if (u.isEmpty()) throw new UserNotFoundException("Utilizador não encontrado.");
        String uid = u.get().getUuid();
        boolean isMember = team.getMembers().stream().anyMatch(m -> uid.equals(m.getUuid()));
        if (!isMember) throw new SecurityException("Acesso negado à equipe.");
    }

    private void assertMemberOfProject(String projectUuid, String email) {
        Projects p = projectsRepository.findById(projectUuid)
                .orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado com o ID: " + projectUuid));
        assertMemberOfTeam(p.getTeamUuid(), email);
    }

    private ProjectResponse toProjectResponse(Projects p) {
        ProjectResponse dto = new ProjectResponse();
        dto.setUuid(p.getUuid());
        dto.setName(p.getName());
        dto.setActive(p.isActive());
        dto.setTeamUuid(p.getTeamUuid());
        dto.setMembers(p.getMembers().stream()
                .map(m -> new ProjectResponse.MemberSummary(m.getUuid(), m.getName(), m.getImg()))
                .collect(Collectors.toList()));
        // Map tasks
        java.util.function.Function<com.api_3.api_3.model.entity.Task.TaskProject, ProjectResponse.TaskSummary> mapTask = tp -> {
            ProjectResponse.TaskSummary ts = new ProjectResponse.TaskSummary();
            ts.setUuid(tp.getUuid());
            ts.setTitle(tp.getTitle());
            ts.setDue_date(tp.getDueDate());
            ts.setStatus(tp.getStatus() != null ? tp.getStatus().name() : null);
            ts.setPriority(tp.getPriority() != null ? tp.getPriority().name() : null);
            ts.setEquip_uuid(tp.getTeamUuid());
            ts.setProject_uuid(tp.getProjectUuid());
            if (tp.getResponsible() != null) {
                dto.getClass(); // keep dto in-scope
                ProjectResponse.ResponsibleSummary rs = new ProjectResponse.ResponsibleSummary(
                        tp.getResponsible().getUuid(),
                        tp.getResponsible().getName(),
                        tp.getResponsible().getImg());
                ts.setResponsible(rs);
            }
            return ts;
        };
        dto.setTasks(p.getTasks() == null ? java.util.List.of() : p.getTasks().stream().map(mapTask).collect(Collectors.toList()));
        dto.setTrashcan(p.getTrashcan() == null ? java.util.List.of() : p.getTrashcan().stream().map(mapTask).collect(Collectors.toList()));
        return dto;
    }

    @GetMapping("/team/{teamUuid}")
    public ResponseEntity<List<ProjectResponse>> listByTeam(@PathVariable String teamUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMemberOfTeam(teamUuid, email);
        List<ProjectResponse> list = projectsRepository.findByTeamUuid(teamUuid).stream()
                .map(this::toProjectResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{projectUuid}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String projectUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            assertMemberOfProject(projectUuid, email);
            Projects p = projectsRepository.findById(projectUuid)
                    .orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado com o ID: " + projectUuid));
            return ResponseEntity.ok(toProjectResponse(p));
        } catch (ProjectNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/team/{teamUuid}")
    public ResponseEntity<ProjectResponse> create(@PathVariable String teamUuid, @Valid @RequestBody CreateProjectRequest req, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMemberOfTeam(teamUuid, email);
        // create project
        Projects p = new Projects();
        p.setUuid(UUID.randomUUID().toString());
        p.setName(req.getName());
        p.setActive(true);
        p.setTeamUuid(teamUuid);
        // Add all team members as members of the project initially
        Teams team = teamsRepository.findById(teamUuid).orElseThrow();
        p.setMembers(team.getMembers());
        Projects saved = projectsRepository.save(p);

        // also add project ref to team
        team.getProjects().add(saved.toRef());
        teamsRepository.save(team);

        return ResponseEntity.status(HttpStatus.CREATED).body(toProjectResponse(saved));
    }

    @PutMapping("/{projectUuid}")
    public ResponseEntity<ProjectResponse> update(@PathVariable String projectUuid, @Valid @RequestBody UpdateProjectRequest req, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMemberOfProject(projectUuid, email);
    Projects p = projectsRepository.findById(projectUuid).orElseThrow();
    p.setName(req.getName());
    if (req.getActive() != null) p.setActive(req.getActive());
    Projects saved = projectsRepository.save(p);
    // sync name in team's project refs
    Teams team = teamsRepository.findById(saved.getTeamUuid()).orElse(null);
    if (team != null) {
        team.setProjects(team.getProjects().stream().map(pr ->
            pr.getUuid().equals(saved.getUuid()) ? new Projects.ProjectRef(saved.getUuid(), saved.getName(), saved.isActive()) : pr
        ).collect(Collectors.toList()));
        teamsRepository.save(team);
    }
    return ResponseEntity.ok(toProjectResponse(saved));
    }

    @PostMapping("/{projectUuid}/archive")
    public ResponseEntity<ProjectResponse> archive(@PathVariable String projectUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMemberOfProject(projectUuid, email);
        Projects p = projectsRepository.findById(projectUuid).orElseThrow();
        p.setActive(false);
        p = projectsRepository.save(p);
        return ResponseEntity.ok(toProjectResponse(p));
    }

    @PostMapping("/{projectUuid}/activate")
    public ResponseEntity<ProjectResponse> activate(@PathVariable String projectUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMemberOfProject(projectUuid, email);
        Projects p = projectsRepository.findById(projectUuid).orElseThrow();
        p.setActive(true);
        p = projectsRepository.save(p);
        return ResponseEntity.ok(toProjectResponse(p));
    }
}
