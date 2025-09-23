package com.api_3.api_3.equipe.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Membro {
    private String uuid;
    private String name;
    private String img;
    private int atribuidas_tasks;
    private int concluidas_tasks;
    private int vencidas_tasks;
}