package com.api_3.api_3.service;

import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleCalendarService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String BASE_URL = "https://www.googleapis.com/calendar/v3/calendars/%s/events";

    private String calendarId(User user) {
        return "primary";
    }

    public String createOrUpdateEvent(User user, Task task) throws IOException, InterruptedException {
        if (user == null || user.getGoogleAccessToken() == null)
            return null;

        String url = String.format(BASE_URL, calendarId(user));
        Map<String, Object> body = new HashMap<>();
        body.put("summary", task.getTitle());
        body.put("description", task.getDescription());

        Date due = task.getDueDate();
        if (due != null) {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            String dueIso = isoFormat.format(due);

            Map<String, String> start = new HashMap<>();
            Map<String, String> end = new HashMap<>();

            start.put("dateTime", dueIso);
            end.put("dateTime", dueIso);

            String zone = ZoneId.systemDefault().toString();
            start.put("timeZone", zone);
            end.put("timeZone", zone);

            body.put("start", start);
            body.put("end", end);
        }

        HttpRequest request;
        if (task.getGoogleEventId() != null && !task.getGoogleEventId().isBlank()) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/" + task.getGoogleEventId()))
                    .header("Authorization", "Bearer " + user.getGoogleAccessToken())
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
        } else {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + user.getGoogleAccessToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
        }

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            Map<String, Object> json = mapper.readValue(
                    resp.body(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });

            Object id = json.get("id");
            return id != null ? id.toString() : null;
        }

        System.err.println("GoogleCalendarService error: " + resp.statusCode() + " - " + resp.body());
        return null;
    }

    public void deleteEvent(User user, String eventId) throws IOException, InterruptedException {
        if (user == null || user.getGoogleAccessToken() == null || eventId == null || eventId.isBlank())
            return;

        String deleteUrl = String.format(BASE_URL, calendarId(user)) + "/" + eventId;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(deleteUrl))
                .header("Authorization", "Bearer " + user.getGoogleAccessToken())
                .DELETE()
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() >= 300) {
            System.err.println("GoogleCalendarService delete error: " + res.statusCode() + " - " + res.body());
        }
    }

    public String listEvents(User user, String timeMin, String timeMax) throws IOException, InterruptedException {
        if (user == null || user.getGoogleAccessToken() == null)
            return "[]";

        StringBuilder sb = new StringBuilder(String.format(BASE_URL, calendarId(user)))
                .append("?singleEvents=true&orderBy=startTime");

        if (timeMin != null && !timeMin.isBlank())
            sb.append("&timeMin=").append(timeMin);
        if (timeMax != null && !timeMax.isBlank())
            sb.append("&timeMax=").append(timeMax);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(sb.toString()))
                .header("Authorization", "Bearer " + user.getGoogleAccessToken())
                .GET()
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }
}