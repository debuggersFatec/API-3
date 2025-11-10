package com.api_3.api_3.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api_3.api_3.dto.request.AuthRequest;
import com.api_3.api_3.dto.request.PasswordRequestEmailDto;
import com.api_3.api_3.dto.request.PasswordResetRequest; 
import com.api_3.api_3.dto.response.AuthResponse;
import com.api_3.api_3.exception.EmailAlreadyExistsException;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.service.AuthService;
import com.api_3.api_3.service.PasswordResetService; 

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public AuthResponse authenticateAndGetToken(@Valid @RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User newUser) {
        try {
            AuthResponse response = authService.register(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao registrar usuário."));
        }
    }

    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(
            @Valid @RequestBody PasswordRequestEmailDto request) {
        try {
            passwordResetService.recoverPassword(request.email());
            // Sempre retorna OK para não vazar informação se o e-mail existe ou não
            return ResponseEntity.ok(Map.of("message", "Se o e-mail estiver cadastrado, um link de redefinição será enviado."));
        } catch (Exception e) {
            // Mesmo em caso de erro, não vaze informações.
             return ResponseEntity.ok(Map.of("message", "Se o e-mail estiver cadastrado, um link de redefinição será enviado."));
        }
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<?> resetPassword(
            @PathVariable String token,
            @Valid @RequestBody PasswordResetRequest request) {
        try {
            passwordResetService.resetPassword(request.newPassword(), token);
            return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso."));
        } catch (IllegalArgumentException e) {
            // Este erro (token inválido/expirado) pode ser mostrado ao usuário
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao resetar a senha."));
        }
    }

    @GetMapping("/reset-password/validate/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        try {
            String email = passwordResetService.validateToken(token);
            return ResponseEntity.ok(Map.of("message", "Token válido.", "email", email.replaceAll("(?<=.{2}).(?=.*@)", "*")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}