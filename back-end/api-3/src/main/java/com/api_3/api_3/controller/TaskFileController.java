package com.api_3.api_3.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart; // Importante: Trocado de RequestParam para RequestPart
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api_3.api_3.dto.response.TaskResponse;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.mapper.TaskMapper;
import com.api_3.api_3.model.embedded.FileAttachment;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/tasks/{taskId}/files")
public class TaskFileController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private TaskMapper taskMapper;

    // --- Helpers de Validação ---
    
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new SecurityException("Usuário não autenticado.");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuário autenticado não encontrado."));
    }

    private void assertProjectMember(Task task, User user) {
        if (task.getProjectUuid() == null) {
            throw new ProjectNotFoundException("Tarefa não associada a um projeto válido.");
        }
        
        Projects project = projectsRepository.findById(task.getProjectUuid())
            .orElseThrow(() -> new ProjectNotFoundException("Projeto não encontrado."));

        boolean isMember = project.getMembers() != null && project.getMembers().stream()
            .anyMatch(memberRef -> memberRef != null && memberRef.getUuid().equals(user.getUuid()));

        if (!isMember) {
            throw new SecurityException("Acesso negado. Você não é membro do projeto desta tarefa.");
        }
    }

    // Rota de Upload (CORRIGIDA PARA SWAGGER)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFiles(
            @PathVariable String taskId,
            @RequestPart("files") List<MultipartFile> files, 
            Authentication authentication) {
        
        try {
            User currentUser = getCurrentUser(authentication);
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com ID: " + taskId));

            assertProjectMember(task, currentUser);

            List<FileAttachment> uploadedAttachments = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                FileAttachment attachment = fileStorageService.storeFile(file, currentUser.getUuid());
                uploadedAttachments.add(attachment);
            }

            if (task.getRequiredFile() == null) {
                task.setRequiredFile(new ArrayList<>());
            }
            
            task.getRequiredFile().addAll(uploadedAttachments);
            Task savedTask = taskRepository.save(task);

            TaskResponse response = taskMapper.toTaskResponse(savedTask);
            return ResponseEntity.ok(response);

        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar upload: " + e.getMessage());
        }
    }

    // Rota de Download (Etapa 4 já incluída)
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String taskId,
            @PathVariable String fileName,
            HttpServletRequest request,
            Authentication authentication) {

        try {
            User currentUser = getCurrentUser(authentication);
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com ID: " + taskId));
            assertProjectMember(task, currentUser);

            Resource resource = fileStorageService.loadFileAsResource(fileName);

            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                // Ignora erro de tipo MIME
            }
            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            // Recupera o nome original para o download
            String downloadName = fileName;
            if (task.getRequiredFile() != null) {
                for (FileAttachment att : task.getRequiredFile()) {
                    if (att.getStoredName().equals(fileName)) {
                        downloadName = att.getOriginalName();
                        break;
                    }
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadName + "\"")
                    .body(resource);

        } catch (TaskNotFoundException | ProjectNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}