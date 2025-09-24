package com.api_3.api_3.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Definimos o status da resposta como NÃO AUTORIZADO
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Definimos o tipo de conteúdo como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Criamos um corpo de resposta personalizado
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Não autorizado");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        // Usamos o ObjectMapper do Jackson (que o Spring usa por baixo dos panos)
        // para escrever o nosso mapa como um JSON na resposta.
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}