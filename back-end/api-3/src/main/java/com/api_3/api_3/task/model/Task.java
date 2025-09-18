package com.api_3.api_3.task.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="tasks")
@Data
@NoArgsConstructor

public class Task {
    @Id
    private String uuid;
    private String title;
    private String description;
    private Date due_date;
    private String status;
    private String priority;
    private String equip_uuid;
    private String file_url;
    private boolean file_required;
    private String file_finish;
    private Responsible responsible;
    private List<Comment> comment;   
}

@Data
@NoArgsConstructor
class Responsible {
    private String uuid;
    private String name;
    private String url_img;

}

@Data
@NoArgsConstructor
class Comment{
    private String content;
    private Date created_at;
    private Author author;
}

@Data
@NoArgsConstructor
class Author{
    private String uuid;
    private String name;
    private String url_img;
}
