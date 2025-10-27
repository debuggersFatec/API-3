package com.api_3.api_3.dto.request;

import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    @Size(min = 2, max = 60, message = "name deve ter entre 2 e 60 caracteres")
    private String name;

    @Size(max = 255, message = "img deve ter no máximo 255 caracteres")
    private String img;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
}
