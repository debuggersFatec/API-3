package com.api_3.api_3.service;

import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class GoogleCalendarService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final UserRepository userRepository;

    public GoogleCalendarService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // -----------------------------
    // REFRESH TOKEN AUTOMÁTICO
    // -----------------------------
    private String refreshAccessToken(User user) throws Exception {
        if (user.getGoogleCalendar().getRefreshToken() == null) {
            throw new RuntimeException("Usuário não possui refresh token. Faça a sincronização novamente.");
        }

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JacksonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(user.getGoogleCalendar().getRefreshToken());

        credential.refreshToken(); // aqui ele troca o refresh token por access token

        user.getGoogleCalendar().setAccessToken(credential.getAccessToken());
        user.getGoogleCalendar().setExpiresAt(Instant.now().plusSeconds(credential.getExpiresInSeconds()));
        userRepository.save(user);

        return credential.getAccessToken();
    }

    // -----------------------------
    // SERVIÇO DO CALENDAR
    // -----------------------------
    private Calendar getCalendarService(User user) throws Exception {

        if (user.getGoogleCalendar().getAccessToken() == null) {
            throw new RuntimeException("Usuário não possui token de acesso. Faça a sincronização.");
        }

        // Se o token expirou, gera um novo automaticamente
        if (user.getGoogleCalendar().getExpiresAt() != null &&
            user.getGoogleCalendar().getExpiresAt().isBefore(Instant.now())) {
            refreshAccessToken(user);
        }

        HttpRequestInitializer initializer = request ->
                request.getHeaders().setAuthorization("Bearer " + user.getGoogleCalendar().getAccessToken());

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                initializer
        ).setApplicationName("API-3").build();
    }

    // -----------------------------
    // OBTER OU CRIAR CALENDÁRIO DO SITE
    // -----------------------------
    private String obterOuCriarCalendarApp(User user) throws Exception {
        if (user.getGoogleCalendar().getCalendarId() != null) {
            return user.getGoogleCalendar().getCalendarId();
        }

        Calendar service = getCalendarService(user);

        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("Debuggers API-3");
        calendar.setTimeZone("America/Sao_Paulo"); // ajuste conforme necessário

        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(calendar).execute();

        user.getGoogleCalendar().setCalendarId(createdCalendar.getId());
        userRepository.save(user);

        return createdCalendar.getId();
    }

    public List<Event> listarEventos(String userUuid) throws Exception {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            Calendar service = getCalendarService(user);
            String calendarId = obterOuCriarCalendarApp(user);
            Events events = service.events().list(calendarId).execute();
            return events.getItems();
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            if (e.getStatusCode() == 401) {
                throw new RuntimeException("Token inválido ou expirado. Faça a sincronização novamente.");
            }
            throw e;
        }
    }

    public Event criarEvento(String userUuid, Event event) throws Exception {
        User user = userRepository.findById(userUuid).orElseThrow();
        Calendar service = getCalendarService(user);
        String calendarId = obterOuCriarCalendarApp(user);
        return service.events().insert(calendarId, event).execute();
    }

    public Event atualizarEvento(String userUuid, String eventId, Event novoEvento) throws Exception {
        User user = userRepository.findById(userUuid).orElseThrow();
        Calendar service = getCalendarService(user);
        String calendarId = obterOuCriarCalendarApp(user);
        return service.events().update(calendarId, eventId, novoEvento).execute();
    }

    public void excluirEvento(String userUuid, String eventId) throws Exception {
        User user = userRepository.findById(userUuid).orElseThrow();
        Calendar service = getCalendarService(user);
        String calendarId = obterOuCriarCalendarApp(user);
        service.events().delete(calendarId, eventId).execute();
    }
}
