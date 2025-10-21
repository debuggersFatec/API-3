package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateProjectRequest {
    @NotBlank
    @Size(min = 2, max = 80)
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
