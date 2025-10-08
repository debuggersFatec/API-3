package com.api_3.api_3.dto.response;

import lombok.Data;
import java.time.Instant;

@Data
public class ErrorResponse {

    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now().toEpochMilli();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}