package com.api_3.api_3.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.mapper.UserMapper;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.security.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

@Service
public class GoogleAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);

    private final UserRepository userRepository;
    private final TeamsRepository teamsRepository;
    private final TaskRepository taskRepository;
    private final AuthResponseBuilder authResponseBuilder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final GoogleIdTokenVerifier verifier;

    public GoogleAuthService(
            UserRepository userRepository,
            TeamsRepository teamsRepository,
        TaskRepository taskRepository,
            AuthResponseBuilder authResponseBuilder,
            UserMapper userMapper,
            JwtUtil jwtUtil,
            @Value("${google.oauth.client-id:}") String clientId
    ) {
        this.userRepository = userRepository;
        this.teamsRepository = teamsRepository;
    this.taskRepository = taskRepository;
        this.authResponseBuilder = authResponseBuilder;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public AuthResponse loginWithIdToken(String idTokenString) {
        if (idTokenString == null || idTokenString.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID token ausente");
        }
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                log.warn("Google ID token verification returned null (provável audience inválida ou token expirado)");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "ID token inválido ou expirado");
            }

            Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            if (email == null || !emailVerified) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail do Google inválido ou não verificado");
            }

            // Upsert user
            User user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
                User u = new User();
                u.setUuid(UUID.randomUUID().toString());
                u.setEmail(email);
                u.setName(name != null ? name : email);
                u.setImg(picture);
                // Evita password nulo; login será via Google
                u.setPassword("");
                return userRepository.save(u);
            });

            boolean changed = false;
            if (name != null && !name.equals(user.getName())) { user.setName(name); changed = true; }
            if (picture != null && (user.getImg() == null || !picture.equals(user.getImg()))) { user.setImg(picture); changed = true; }
            if (user.getPassword() == null) { user.setPassword(""); changed = true; }
            if (changed) userRepository.save(user);

            // Build JWT
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities("USER")
                .build();
            String token = jwtUtil.generateToken(userDetails);

            // Load related info similar to AuthService
            List<Teams> teams = teamsRepository.findAllById(user.getEquipeIds());
            List<Task> tasks = taskRepository.findByResponsibleUuid(user.getUuid());

            AuthResponse.UserInfo userInfo = userMapper.toUserInfo(user, teams, tasks);
            return authResponseBuilder.build(token, userInfo, user.getUuid());
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            // Comum quando JWT_SECRET não é Base64 válido ou token inválido
            log.error("Falha no login com Google: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        } catch (Exception e) {
            log.error("Erro interno no login com Google", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar login com Google");
        }
    }
}
