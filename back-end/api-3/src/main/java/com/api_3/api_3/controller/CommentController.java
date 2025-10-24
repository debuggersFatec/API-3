package com.api_3.api_3.controller;

import com.api_3.api_3.dto.request.CreateCommentRequest;
import com.api_3.api_3.dto.request.UpdateCommentRequest;
import com.api_3.api_3.dto.response.CommentResponse;
import com.api_3.api_3.exception.CommentNotFoundException; // Certifique-se que esta importação está correta
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.CommentMapper; // Importar o CommentMapper
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.TaskComment;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// Remover a importação de Optional se não for mais usada diretamente
// import java.util.Optional;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments") // Base path para os comentários de uma tarefa
public class CommentController {

    @Autowired private CommentService commentService;
    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CommentMapper commentMapper; // Injetar o CommentMapper

    // --- Helper de Autorização ---
    private UserDetails getUserDetails(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new SecurityException("Usuário não autenticado.");
        }
        // Verifica se o principal é uma instância de UserDetails antes de fazer o cast
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new SecurityException("Principal de autenticação inválido.");
        }
        return (UserDetails) authentication.getPrincipal();
    }

    private User getCurrentUser(Authentication authentication) {
         UserDetails userDetails = getUserDetails(authentication);
         return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuário autenticado não encontrado com o email: " + userDetails.getUsername()));
    }

    private void assertProjectMember(String taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com ID: " + taskId));

        if (task.getProjectUuid() == null || task.getProjectUuid().isBlank()) {
             throw new ProjectNotFoundException("Tarefa com ID " + taskId + " não está associada a um projeto.");
        }

        Projects project = projectsRepository.findById(task.getProjectUuid())
            .orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado com ID: " + task.getProjectUuid()));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com o email: " + userEmail));

        boolean isMember = project.getMembers() != null && project.getMembers().stream()
            .anyMatch(memberRef -> memberRef != null && memberRef.getUuid().equals(user.getUuid()));

        if (!isMember) {
            throw new SecurityException("Acesso negado. Usuário não é membro do projeto '" + project.getName() + "'.");
        }
    }

    // --- REMOVER o método mapToResponse daqui ---

    // --- Endpoints CRUD (Usando o CommentMapper injetado) ---

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String taskId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            assertProjectMember(taskId, currentUser.getEmail()); // Verifica se é membro do projeto

            TaskComment createdComment = commentService.addComment(taskId, request.getContent(), currentUser.getUuid());

            // Usar o mapper injetado
            CommentResponse responseDto = commentMapper.toCommentResponse(createdComment);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Ou retornar mensagem de erro
        } catch (SecurityException e) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Ou retornar mensagem de erro
        } catch (Exception e) {
            // Logar o erro e retornar um erro genérico
            System.err.println("Erro ao adicionar comentário: " + e.getMessage()); // Considere usar um Logger SLF4J
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String taskId,
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            assertProjectMember(taskId, currentUser.getEmail()); // Verifica se é membro do projeto

            TaskComment updatedComment = commentService.updateComment(taskId, commentId, request.getContent(), currentUser.getUuid());

            // Usar o mapper injetado
            CommentResponse responseDto = commentMapper.toCommentResponse(updatedComment);

             return ResponseEntity.ok(responseDto);
        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException | CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
             System.err.println("Erro ao atualizar comentário: " + e.getMessage()); // Considere usar um Logger SLF4J
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String taskId,
            @PathVariable String commentId,
            Authentication authentication) {
         try {
            User currentUser = getCurrentUser(authentication);
            assertProjectMember(taskId, currentUser.getEmail()); // Verifica se é membro do projeto

            commentService.deleteComment(taskId, commentId, currentUser.getUuid());
            return ResponseEntity.noContent().build(); // HTTP 204
        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException | CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            System.err.println("Erro ao deletar comentário: " + e.getMessage()); // Considere usar um Logger SLF4J
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}