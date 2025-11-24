package com.api_3.api_3.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GoogleConfig {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    @Bean
    public GoogleAuthorizationCodeFlow googleFlow() throws Exception {
        GoogleClientSecrets secrets = new GoogleClientSecrets()
                .setInstalled(
                        new GoogleClientSecrets.Details()
                                .setClientId(clientId)
                                .setClientSecret(clientSecret)
                );

        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                secrets,
                List.of("https://www.googleapis.com/auth/calendar")
        )
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}