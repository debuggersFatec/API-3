package com.api_3.api_3.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TeamNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.LeaveProjectService;
import com.api_3.api_3.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LeaveProjectService leaveProjectService;

    @Autowired
    private UserRepository userRepository;

    private String currentUserEmail(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {

            throw new SecurityException("Usuário não autenticado ou principal inválido.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @GetMapping("/team/{teamUuid}")
    public ResponseEntity<List<ProjectResponse>> listByTeam(@PathVariable String teamUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            List<ProjectResponse> projects = projectService.listProjectsByTeam(teamUuid, email);
            return ResponseEntity.ok(projects);
        } catch (TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{projectUuid}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String projectUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            ProjectResponse project = projectService.getProjectDetails(projectUuid, email);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException | TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/team/{teamUuid}")
    public ResponseEntity<ProjectResponse> create(@PathVariable String teamUuid, @Valid @RequestBody CreateProjectRequest req, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            ProjectResponse createdProject = projectService.createProject(teamUuid, req, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{projectUuid}")
    public ResponseEntity<ProjectResponse> update(@PathVariable String projectUuid, @Valid @RequestBody UpdateProjectRequest req, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            ProjectResponse updatedProject = projectService.updateProject(projectUuid, req, email);
            return ResponseEntity.ok(updatedProject);
        } catch (ProjectNotFoundException | TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{projectUuid}/archive")
    public ResponseEntity<ProjectResponse> archive(@PathVariable String projectUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            ProjectResponse project = projectService.archiveProject(projectUuid, email);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException | TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{projectUuid}/activate")
    public ResponseEntity<ProjectResponse> activate(@PathVariable String projectUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            ProjectResponse project = projectService.activateProject(projectUuid, email);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException | TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // POST /api/projects/{projectUuid}/members/{userUuid} -> adiciona membro ao projeto (gera PROJECT_MEMBER_JOINED)
    @PostMapping("/{projectUuid}/members/{userUuid}")
    public ResponseEntity<ProjectResponse> addMember(@PathVariable String projectUuid, @PathVariable String userUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            ProjectResponse project = projectService.addMemberToProject(projectUuid, userUuid, email);
            return ResponseEntity.ok(project);
        } catch (ProjectNotFoundException | TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            if (e.getMessage().contains("não é membro da equipe")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .header("Error-Message", e.getMessage())
                        .build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // DELETE /api/projects/{projectUuid}/leave -> usuário sai do projeto (gera PROJECT_MEMBER_LEFT; remove projeto se sem membros)
    @DeleteMapping("/{projectUuid}/leave")
    public ResponseEntity<Void> leaveProject(@PathVariable String projectUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Usuário autenticado não encontrado."));
            leaveProjectService.execute(projectUuid, currentUser.getUuid());
            return ResponseEntity.noContent().build();
        } catch (ProjectNotFoundException | TeamNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}