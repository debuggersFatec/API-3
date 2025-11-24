package com.api_3.api_3.model.entity;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GoogleCalendarRef {

    private boolean connected = false;
    private String accessToken;
    private String refreshToken;
    private Instant expiresAt;
    private String calendarId;

}
