package com.api_3.api_3.mapper;

import com.api_3.api_3.dto.response.CommentResponse;
import com.api_3.api_3.model.entity.TaskComment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component 
public class CommentMapper {
    public CommentResponse toCommentResponse(TaskComment comment) {
        if (comment == null) {
            return null;
        }

        CommentResponse.UserInfo authorInfo = null;
        if (comment.getUser() != null) {
            // Cria o DTO aninhado para informações do autor
            authorInfo = new CommentResponse.UserInfo(
                comment.getUser().getUuid(),
                comment.getUser().getName(),
                comment.getUser().getImg()
            );
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