// src/main/java/com/api_3/api_3/controller/TaskController.java
package com.api_3.api_3.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Já deve existir
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping; // Importar Projects
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.dto.request.CreateTaskRequest;
import com.api_3.api_3.dto.request.UpdateTaskRequest;
import com.api_3.api_3.dto.response.TaskResponse;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.exception.TeamNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.TaskMapper;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.task.CreateTaskService;
import com.api_3.api_3.service.task.DeleteTaskService;
import com.api_3.api_3.service.task.GetTaskService;
import com.api_3.api_3.service.task.UpdateTaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired private CreateTaskService createTaskService;
    @Autowired private GetTaskService getTaskService;
    @Autowired private UpdateTaskService updateTaskService;
    @Autowired private DeleteTaskService deleteTaskService;

    @Autowired private TeamsRepository teamsRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskMapper taskMapper;

    // --- Helpers ---
    private UserDetails getUserDetails(Authentication authentication) {
         if (authentication == null || authentication.getPrincipal() == null) {
            // Lançar ou tratar adequadamente - talvez retornar null e verificar nos métodos
             throw new SecurityException("Usuário não autenticado.");
        }
        // Verifica se o principal é uma instância de UserDetails antes de fazer o cast
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new SecurityException("Principal de autenticação inválido.");
        }
        return (UserDetails) authentication.getPrincipal();
    }

    // Método antigo - manter se ainda precisar para createTask
    private void assertTeamMember(String teamUuid, String email) {
        Teams team = teamsRepository.findById(teamUuid)
                .orElseThrow(() -> new TeamNotFoundException("Team não encontrado com o ID: " + teamUuid));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilizador não encontrado com o email: " + email));
        boolean isMember = team.getMembers() != null && team.getMembers().stream()
                .anyMatch(m -> m != null && m.getUuid().equals(user.getUuid()));
        if (!isMember) {
            throw new SecurityException("Acesso negado à equipe '" + team.getName() + "'.");
        }
    }

    // Novo método de verificação de membro do PROJETO
    private void assertProjectMember(String taskId, String userEmail) {
        Task task = getTaskService.findById(taskId); // Reutiliza o serviço que já lança TaskNotFoundException

        if (task.getProjectUuid() == null || task.getProjectUuid().isBlank()) {
            throw new ProjectNotFoundException("Tarefa com ID " + taskId + " não está associada a um projeto válido.");
        }

        Projects project = projectsRepository.findById(task.getProjectUuid())
            .orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado com ID: " + task.getProjectUuid() + ", associado à tarefa " + taskId));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UserNotFoundException("Usuário autenticado não encontrado com o email: " + userEmail));

        boolean isMember = project.getMembers() != null && project.getMembers().stream()
            .anyMatch(memberRef -> memberRef != null && memberRef.getUuid().equals(user.getUuid()));

        if (!isMember) {
            throw new SecurityException("Acesso negado. Usuário não é membro do projeto '" + project.getName() + "'.");
        }
    }

    // --- Endpoints ---

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            // Validação para CRIAÇÃO: Verifica se o usuário é membro da EQUIPE do projeto
            Projects project = projectsRepository.findById(request.getProject_uuid())
                    .orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado com o ID: " + request.getProject_uuid()));

            // Usar assertTeamMember aqui ainda faz sentido para criação
            assertTeamMember(project.getTeamUuid(), userDetails.getUsername());

            Task savedTask = createTaskService.execute(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toTaskResponse(savedTask));

        } catch (ProjectNotFoundException | TeamNotFoundException | UserNotFoundException e) {
             // Logar e retornar erro apropriado
             System.err.println("Erro ao criar tarefa: " + e.getMessage());
             // Poderia retornar um corpo de erro mais específico se desejado
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Bad Request pois o ID do projeto/equipe pode estar inválido
        } catch (SecurityException e) {
             System.err.println("Erro de segurança ao criar tarefa: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
             System.err.println("Erro inesperado ao criar tarefa: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            // **ALTERADO:** Usar assertProjectMember para leitura
            assertProjectMember(uuid, userDetails.getUsername());

            Task task = getTaskService.findById(uuid); // findById já é chamado dentro de assertProjectMember, mas chamamos de novo para clareza
            return ResponseEntity.ok(taskMapper.toTaskResponse(task));

        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException e) {
            // Logar e retornar erro apropriado
            System.err.println("Erro ao buscar tarefa por ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
             System.err.println("Erro de segurança ao buscar tarefa por ID: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
             System.err.println("Erro inesperado ao buscar tarefa por ID: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count-by-status")
    public ResponseEntity<List<Map<String, Object>>> getTaskCountByStatus() {
        // Este endpoint parece ser público ou de agregação geral, não necessita de validação de membro por enquanto.
        try {
            List<Map<String, Object>> counts = getTaskService.countByStatus();
            return ResponseEntity.ok(counts);
        } catch (Exception e) {
             System.err.println("Erro inesperado ao contar tarefas por status: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable String uuid, @Valid @RequestBody UpdateTaskRequest request, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
             // **ALTERADO:** Usar assertProjectMember para atualização
            assertProjectMember(uuid, userDetails.getUsername());

            // Nota: O UpdateTaskService internamente valida se o novo responsável pertence à equipe.
            // Se a regra mudar para "novo responsável deve pertencer ao projeto", essa validação precisaria ser ajustada no Service.
            Task updatedTask = updateTaskService.execute(uuid, request);
            return ResponseEntity.ok(taskMapper.toTaskResponse(updatedTask));

        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException e) {
             System.err.println("Erro ao atualizar tarefa: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
             System.err.println("Erro de segurança ao atualizar tarefa: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) { // Captura outras exceções como InvalidResponsibleException do service
             System.err.println("Erro ao atualizar tarefa: " + e.getMessage());
             // Pode ser BAD_REQUEST dependendo da exceção vinda do service
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Ou INTERNAL_SERVER_ERROR para erros inesperados
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable String uuid, Authentication authentication) {
        UserDetails userDetails = getUserDetails(authentication);
        try {
            // **ALTERADO:** Usar assertProjectMember para exclusão
            assertProjectMember(uuid, userDetails.getUsername());

            Task deletedTask = deleteTaskService.execute(uuid);
            // Retorna a tarefa marcada como DELETED, conforme implementação do service
            return ResponseEntity.ok(taskMapper.toTaskResponse(deletedTask));

        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException e) {
             System.err.println("Erro ao deletar tarefa: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
             System.err.println("Erro de segurança ao deletar tarefa: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
             System.err.println("Erro inesperado ao deletar tarefa: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}