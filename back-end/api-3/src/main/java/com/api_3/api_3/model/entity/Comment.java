package com.api_3.api_3.model.entity;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Comment {
    private String content;
    private Date created_at;
    private Author author;
}