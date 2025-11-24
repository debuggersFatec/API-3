package com.api_3.api_3.controller;

import com.api_3.api_3.dto.response.CalendarResponse;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.GoogleAuthService;
import com.api_3.api_3.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendar")
public class GoogleCalendarController {

    private final GoogleAuthService authService;
    private final GoogleCalendarService calendarService;
    private final UserRepository userRepository;

    public GoogleCalendarController(GoogleAuthService authService,
                                    GoogleCalendarService calendarService,
                                    UserRepository userRepository) {
        this.authService = authService;
        this.calendarService = calendarService;
        this.userRepository = userRepository;
    }

    // -----------------------------
    // Helper para validar UUID do usuário logado
    // -----------------------------
    private User getValidatedUser(String userUuid, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User loggedUser = userRepository.findByEmail(userDetails.getUsername())
                                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!loggedUser.getUuid().equals(userUuid)) {
            throw new RuntimeException("Você não tem permissão para acessar este recurso");
        }
        return loggedUser;
    }

    // -----------------------------
    // 1️⃣ Redirecionar para OAuth do Google
    // -----------------------------
    @GetMapping("/auth/google/{userUuid}")
    public void redirectToGoogleAuth(@PathVariable String userUuid, 
                                     Authentication authentication,
                                     HttpServletResponse response) throws Exception {

        getValidatedUser(userUuid, authentication);

        String url = authService.gerarUrlAutenticacao(userUuid);
        response.sendRedirect(url);
    }

    // -----------------------------
    // 2️⃣ Callback OAuth2
    // -----------------------------
    @GetMapping("/auth/callback")
    public void callback(@RequestParam String code,
                         @RequestParam String state,
                         HttpServletResponse response) throws Exception {

        authService.salvarTokens(state, code);
        response.sendRedirect("http://localhost:5173"); 
    }

    // -----------------------------
    // 3️⃣ Listar eventos
    // -----------------------------
    @GetMapping("/events/{userUuid}")
    public ResponseEntity<?> listar(@PathVariable String userUuid, Authentication authentication) {
        try {
            getValidatedUser(userUuid, authentication);

            List<Event> events = calendarService.listarEventos(userUuid);
            List<CalendarResponse> response = events.stream()
                                                    .map(CalendarResponse::fromEvent)
                                                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao acessar Google Calendar: " + e.getMessage());
        }
    }

    // -----------------------------
    // 4️⃣ Criar evento
    // -----------------------------
    @PostMapping("/events/{userUuid}")
    public ResponseEntity<?> criar(@PathVariable String userUuid,
                                   @RequestBody CalendarResponse eventDTO,
                                   Authentication authentication) {
        try {
            getValidatedUser(userUuid, authentication);

            Event event = eventDTO.toEvent();
            Event criado = calendarService.criarEvento(userUuid, event);
            return ResponseEntity.ok(CalendarResponse.fromEvent(criado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar evento: " + e.getMessage());
        }
    }

    // -----------------------------
    // 5️⃣ Atualizar evento
    // -----------------------------
    @PutMapping("/events/{userUuid}/{eventId}")
    public ResponseEntity<?> atualizar(@PathVariable String userUuid,
                                       @PathVariable String eventId,
                                       @RequestBody CalendarResponse eventDTO,
                                       Authentication authentication) {
        try {
            getValidatedUser(userUuid, authentication);

            Event event = eventDTO.toEvent();
            Event atualizado = calendarService.atualizarEvento(userUuid, eventId, event);
            return ResponseEntity.ok(CalendarResponse.fromEvent(atualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao atualizar evento: " + e.getMessage());
        }
    }

    // -----------------------------
    // 6️⃣ Excluir evento
    // -----------------------------
    @DeleteMapping("/events/{userUuid}/{eventId}")
    public ResponseEntity<?> excluir(@PathVariable String userUuid,
                                     @PathVariable String eventId,
                                     Authentication authentication) {
        try {
            getValidatedUser(userUuid, authentication);

            calendarService.excluirEvento(userUuid, eventId);
            return ResponseEntity.ok("Evento excluído com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao excluir evento: " + e.getMessage());
        }
    }
}