package com.api_3.api_3.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class UserResponse {
    private String uuid;
    private String name;
    private String email;
    private String img;
    private GoogleCalendarInfo googleCalendar;
    private List<String> equipeIds;

    @Data
    public static class GoogleCalendarInfo {
        private boolean connected;
        private String accessToken;
        private String refreshToken;
        private java.time.Instant expiresAt;
        private String calendarId;
    }
}