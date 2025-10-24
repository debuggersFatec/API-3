package com.api_3.api_3.mapper;

import com.api_3.api_3.dto.response.CommentResponse;
import com.api_3.api_3.model.entity.TaskComment;
import com.api_3.api_3.model.entity.User; 
import com.api_3.api_3.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional; 
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    @Autowired
    private UserRepository userRepository;

    public CommentResponse toCommentResponse(TaskComment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponse.UserInfo authorInfo = null;
        if (comment.getAuthorUuid() != null) {
            Optional<User> authorOpt = userRepository.findById(comment.getAuthorUuid());
            if (authorOpt.isPresent()) {
                User author = authorOpt.get();
                // Cria o DTO aninhado para informações do autor COM DADOS ATUAIS
                authorInfo = new CommentResponse.UserInfo(
                    author.getUuid(),
                    author.getName(),
                    author.getImg()
                );
            } else {
                 authorInfo = new CommentResponse.UserInfo(comment.getAuthorUuid(), "Usuário Removido", null);
            }
        }

        return new CommentResponse(
            comment.getUuid(),
            comment.getComment(),
            comment.getCreatedAt(),
            authorInfo
        );
    }

    public List<CommentResponse> toCommentResponseList(List<TaskComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }

        return comments.stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());
    }
}