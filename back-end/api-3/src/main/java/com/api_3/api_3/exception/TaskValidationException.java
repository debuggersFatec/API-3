package com.api_3.api_3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TaskValidationException extends RuntimeException {
    public TaskValidationException(String message) {
        super(message);
    }
}