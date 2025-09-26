package com.api_3.api_3.exception;

import java.util.Map; // Importar a classe Map
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- Exceções de Utilizador e Autenticação ---
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, String> response = Map.of("erro", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        Map<String, String> response = Map.of("erro", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        Map<String, String> response = Map.of("erro", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    //  Exceções de Pedido Inválido (Genérica) 
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequestException(InvalidRequestException ex) {
        Map<String, String> response = Map.of("erro", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //  Exceções de Equipa 
    @ExceptionHandler(EquipeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEquipeNotFoundException(EquipeNotFoundException ex) {
        Map<String, String> response = Map.of("erro", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Exceção de Membro Já Existe
    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<?> memberAlreadyExistsException(MemberAlreadyExistsException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // HTTP 409 Conflict é ideal para este caso
    }

    @ExceptionHandler(EquipeBadRequestException.class)
    public ResponseEntity<Map<String, String>> handleEquipeBadRequestException(EquipeBadRequestException ex) {
        Map<String, String> response = Map.of("erro", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // --- Apanhador Genérico ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        logger.error("Ocorreu um erro inesperado: ", ex);
        Map<String, String> response = Map.of("erro", "Ocorreu um erro interno no servidor.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}