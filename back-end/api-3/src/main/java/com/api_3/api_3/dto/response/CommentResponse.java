package com.api_3.api_3.dto.response;

import java.util.Date;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String uuid;
    private String comment;
    private Date createdAt;
    private UserInfo author; 

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String uuid;
        private String name;
        private String img;
    }
}