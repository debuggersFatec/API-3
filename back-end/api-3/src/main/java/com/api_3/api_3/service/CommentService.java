package com.api_3.api_3.service;

import com.api_3.api_3.exception.CommentNotFoundException;
import com.api_3.api_3.exception.TaskNotFoundException;
import com.api_3.api_3.exception.UserNotFoundException;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.TaskComment;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.ArrayList; 

@Service
public class CommentService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TaskComment addComment(String taskId, String content, String authorUuid) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com ID: " + taskId));

        User author = userRepository.findById(authorUuid)
                .orElseThrow(() -> new UserNotFoundException("Autor do comentário não encontrado com ID: " + authorUuid));

        TaskComment newComment = new TaskComment(content, new Date(), author.toRef());

        if (task.getComments() == null) {
            task.setComments(new ArrayList<>());
        }

        task.getComments().add(newComment);
        taskRepository.save(task);

        return newComment;
    }

    @Transactional
    public TaskComment updateComment(String taskId, String commentId, String updatedContent, String requestingUserUuid) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com ID: " + taskId));

        if (task.getComments() == null) {
            throw new CommentNotFoundException("Comentário não encontrado com ID: " + commentId + " na tarefa " + taskId);
        }

        TaskComment commentToUpdate = task.getComments().stream()
                .filter(c -> c.getUuid().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comentário não encontrado com ID: " + commentId + " na tarefa " + taskId));

        // Verifica a autoria
        if (!commentToUpdate.getUser().getUuid().equals(requestingUserUuid)) {
            throw new SecurityException("Utilizador não autorizado a editar este comentário.");
        }

        commentToUpdate.setComment(updatedContent);
        taskRepository.save(task);

        return commentToUpdate;
    }

    @Transactional
    public void deleteComment(String taskId, String commentId, String requestingUserUuid) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada com ID: " + taskId));

        if (task.getComments() == null) {
             throw new CommentNotFoundException("Comentário não encontrado com ID: " + commentId + " na tarefa " + taskId);
        }

        TaskComment commentToDelete = task.getComments().stream()
            .filter(c -> c.getUuid().equals(commentId))
            .findFirst()
            .orElseThrow(() -> new CommentNotFoundException("Comentário não encontrado com ID: " + commentId + " na tarefa " + taskId));

        // Verifica a autoria
        if (!commentToDelete.getUser().getUuid().equals(requestingUserUuid)) {
            throw new SecurityException("Utilizador não autorizado a excluir este comentário.");
        }

        boolean removed = task.getComments().removeIf(c -> c.getUuid().equals(commentId));

        if (removed) {
            taskRepository.save(task);
        } else {
             throw new CommentNotFoundException("Falha ao remover comentário com ID: " + commentId + " da tarefa " + taskId);
        }
    }
}