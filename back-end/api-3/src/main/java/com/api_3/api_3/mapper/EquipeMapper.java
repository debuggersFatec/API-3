package com.api_3.api_3.mapper;

import com.api_3.api_3.dto.response.EquipeResponse;
import com.api_3.api_3.dto.response.MembroResponse;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Membro;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.List;

@Component
public class EquipeMapper {

    public EquipeResponse toEquipeResponse(Equipe equipe) {
        if (equipe == null) {
            return null;
        }

        EquipeResponse dto = new EquipeResponse();
        dto.setUuid(equipe.getUuid());
        dto.setName(equipe.getName());
        dto.setTasks(equipe.getTasks());

        if (equipe.getMembros() != null) {
            dto.setMembros(equipe.getMembros().stream()
                    .map(this::toMembroResponse)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<EquipeResponse> toEquipeResponseList(List<Equipe> equipes) {
        return equipes.stream()
                .map(this::toEquipeResponse)
                .collect(Collectors.toList());
    }

    private MembroResponse toMembroResponse(Membro membro) {
        if (membro == null) {
            return null;
        }

        MembroResponse dto = new MembroResponse();
        dto.setUuid(membro.getUuid());
        dto.setName(membro.getName());
        dto.setImg(membro.getImg());

        return dto;
    }
}