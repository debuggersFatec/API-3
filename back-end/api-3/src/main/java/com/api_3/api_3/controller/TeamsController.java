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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.dto.request.CreateTeamRequest;
import com.api_3.api_3.dto.request.UpdateTeamRequest;
import com.api_3.api_3.dto.response.TeamResponse;
import com.api_3.api_3.exception.TeamNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.JwtUtil;
import com.api_3.api_3.service.NotificationService;
import com.api_3.api_3.service.task.TaskMaintenanceService;
import com.api_3.api_3.service.team.LeaveTeamService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams")
public class TeamsController {

    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private TaskMaintenanceService taskMaintenanceService;
    @Autowired private LeaveTeamService leaveTeamService;
    @Autowired private NotificationService notificationService;
    @Autowired private JwtUtil jwtUtil;

    private String currentUserEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private void assertMember(String teamUuid, String email) {
    Teams team = teamsRepository.findById(teamUuid)
    .orElseThrow(() -> new TeamNotFoundException("Team não encontrado com o ID: " + teamUuid));
    String currentUserUuid = userRepository.findByEmail(email).map(User::getUuid)
        .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado."));
        boolean isMember = team.getMembers().stream().anyMatch(m -> currentUserUuid.equals(m.getUuid()));
        if (!isMember) throw new SecurityException("Not a member of this team");
    }

    private TeamResponse toTeamResponse(Teams team) {
    TeamResponse dto = new TeamResponse();
    dto.setUuid(team.getUuid());
    dto.setName(team.getName());
    java.util.List<User.UserRef> members = team.getMembers() != null ? team.getMembers() : java.util.Collections.<User.UserRef>emptyList();
    dto.setMembers(members.stream()
        .map(m -> new TeamResponse.MemberSummary(m.getUuid(), m.getName(), m.getImg()))
        .collect(Collectors.toList()));
    java.util.List<Projects.ProjectRef> projects = team.getProjects() != null ? team.getProjects() : java.util.Collections.<Projects.ProjectRef>emptyList();
    dto.setProjects(projects.stream()
        .map(p -> new TeamResponse.ProjectSummary(p.getUuid(), p.getName(), p.getIsActive()))
        .collect(Collectors.toList()));
    return dto;
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> myTeams(Authentication authentication) {
        String email = currentUserEmail(authentication);
        User me = userRepository.findByEmail(email).orElseThrow();
        List<TeamResponse> list = me.getTeams().stream()
                .map(ref -> teamsRepository.findById(ref.getUuid()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toTeamResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{teamUuid}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable String teamUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            assertMember(teamUuid, email);
            Teams team = teamsRepository.findById(teamUuid)
                    .orElseThrow(() -> new TeamNotFoundException("Team não encontrado com o ID: " + teamUuid));
            return ResponseEntity.ok(toTeamResponse(team));
    } catch (TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest req, Authentication authentication) {
        String email = currentUserEmail(authentication);
        User me = userRepository.findByEmail(email).orElseThrow();

        Teams team = new Teams();
        team.setUuid(UUID.randomUUID().toString());
        team.setName(req.getName());
        team.getMembers().add(me.toRef());
        team = teamsRepository.save(team);

        // add to user's list
        me.getTeams().add(team.toRef());
        userRepository.save(me);

        return ResponseEntity.status(HttpStatus.CREATED).body(toTeamResponse(team));
    }

    @PutMapping("/{teamUuid}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable String teamUuid, @Valid @RequestBody UpdateTeamRequest req, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMember(teamUuid, email);
        Teams team = teamsRepository.findById(teamUuid).orElseThrow();
        team.setName(req.getName());
        team = teamsRepository.save(team);
        return ResponseEntity.ok(toTeamResponse(team));
    }

    @DeleteMapping("/{teamUuid}")
    public ResponseEntity<Void> deleteTeam(@PathVariable String teamUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMember(teamUuid, email);
        Teams team = teamsRepository.findById(teamUuid).orElseThrow();

        // remove from members' team lists
        for (User.UserRef memberRef : team.getMembers()) {
            userRepository.findById(memberRef.getUuid()).ifPresent(u -> {
                u.setTeams(u.getTeams().stream().filter(tr -> !teamUuid.equals(tr.getUuid())).collect(Collectors.toList()));
                userRepository.save(u);
            });
        }
        teamsRepository.deleteById(teamUuid);
        return ResponseEntity.noContent().build();
    }

    // POST /api/teams/{teamUuid}/members/{userUuid} -> adiciona membro à equipe (gera TEAM_MEMBER_JOINED)
    @PostMapping("/{teamUuid}/members/{userUuid}")
    public ResponseEntity<TeamResponse> addMember(@PathVariable String teamUuid, @PathVariable String userUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMember(teamUuid, email);
        Teams team = teamsRepository.findById(teamUuid).orElseThrow();
        User user = userRepository.findById(userUuid).orElseThrow();
        boolean already = team.getMembers().stream().anyMatch(m -> userUuid.equals(m.getUuid()));
        if (!already) {
            team.getMembers().add(user.toRef());
            teamsRepository.save(team);

            user.getTeams().add(team.toRef());
            userRepository.save(user);
            // Notificar membros do time sobre a entrada
            notificationService.notifyTeamMemberJoined(teamUuid, userUuid);
        }
        return ResponseEntity.ok(toTeamResponse(team));
    }

    // DELETE /api/teams/{teamUuid}/members/{userUuid} -> remove membro da equipe (gera TEAM_MEMBER_LEFT; exclui equipe se sem membros)
    @DeleteMapping("/{teamUuid}/members/{userUuid}")
    public ResponseEntity<TeamResponse> removeMember(@PathVariable String teamUuid, @PathVariable String userUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMember(teamUuid, email);
        Teams team = teamsRepository.findById(teamUuid).orElseThrow();
        team.setMembers(team.getMembers().stream().filter(m -> !userUuid.equals(m.getUuid())).collect(Collectors.toList()));
        // If no members remain, delete the team and return 204
        if (team.getMembers() == null || team.getMembers().isEmpty()) {
            teamsRepository.deleteById(teamUuid);
            // Clean up the user's team reference as well
            userRepository.findById(userUuid).ifPresent(u -> {
                u.setTeams(u.getTeams().stream().filter(tr -> !teamUuid.equals(tr.getUuid())).collect(Collectors.toList()));
                userRepository.save(u);
            });
            // Unassign tasks for the team scope
            taskMaintenanceService.unassignForTeam(teamUuid, userUuid);
            return ResponseEntity.noContent().build();
        } else {
            teamsRepository.save(team);
            // Notificar membros do time sobre a saída
            notificationService.notifyTeamMemberLeft(teamUuid, userUuid);
        }

        userRepository.findById(userUuid).ifPresent(u -> {
            u.setTeams(u.getTeams().stream().filter(tr -> !teamUuid.equals(tr.getUuid())).collect(Collectors.toList()));
            userRepository.save(u);
        });

        // Unassign tasks where this user was responsible within this team
        taskMaintenanceService.unassignForTeam(teamUuid, userUuid);
        return ResponseEntity.ok(toTeamResponse(team));
    }

    @GetMapping("/{teamUuid}/projects")
    public ResponseEntity<List<Projects>> listProjects(@PathVariable String teamUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        assertMember(teamUuid, email);
        List<Projects> projects = projectsRepository.findByTeamUuid(teamUuid);
        return ResponseEntity.ok(projects);
    }

    // DELETE /api/teams/{teamUuid}/leave -> usuário sai da equipe (gera TEAM_MEMBER_LEFT)
    @DeleteMapping("/{teamUuid}/leave")
    public ResponseEntity<Void> leaveTeam(@PathVariable String teamUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Usuário autenticado não encontrado."));
        
        // UTILIZAR O SERVICE
        leaveTeamService.execute(teamUuid, currentUser.getUuid());

        return ResponseEntity.noContent().build(); // Resposta 204 No Content
    }

    // Endpoint para Gerar Token de Convite
    // POST /api/teams/{teamUuid}/invite -> gera token de convite para ingresso
    @PostMapping("/{teamUuid}/invite")
    public ResponseEntity<String> generateInviteToken(@PathVariable String teamUuid, Authentication authentication) {
        String email = currentUserEmail(authentication);
        try {
            assertMember(teamUuid, email);
            String token = jwtUtil.generateInviteToken(teamUuid);
            return ResponseEntity.ok("{\"token\": \"" + token + "\"}");
        } catch (TeamNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // EndPoint Para Entrar na equipe com o token 
    // POST /api/teams/join/invite/{token} -> entra na equipe usando token (gera TEAM_MEMBER_JOINED)
    @PostMapping("/join/invite/{token}")
    public ResponseEntity<TeamResponse> joinTeamWithInvite(@PathVariable String token, Authentication authentication) {
        String email = currentUserEmail(authentication);
        String teamUuid;

        try {
            teamUuid = jwtUtil.extractInviteTeamId(token);
            if (teamUuid == null || teamUuid.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Error-Message", "Token inválido ou malformado.").build();
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Error-Message", "Token de convite expirado.").build();
        } catch (SignatureException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Error-Message", "Token de convite inválido ou adulterado.").build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Error-Message", "Erro ao processar o token.").build();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.api_3.api_3.exception.UserNotFoundException("Utilizador não encontrado."));

        Teams team = teamsRepository.findById(teamUuid)
                .orElseThrow(() -> new TeamNotFoundException("Team não encontrado com o ID: " + teamUuid));

        boolean alreadyMember = team.getMembers().stream().anyMatch(m -> user.getUuid().equals(m.getUuid()));
        if (alreadyMember) {
            return ResponseEntity.ok(toTeamResponse(team));
        }

        team.getMembers().add(user.toRef());
        teamsRepository.save(team);

        user.getTeams().add(team.toRef());
        userRepository.save(user);

        // Notificar membros do time sobre a entrada por convite
        notificationService.notifyTeamMemberJoined(teamUuid, user.getUuid());

        return ResponseEntity.ok(toTeamResponse(team));
    }
}
