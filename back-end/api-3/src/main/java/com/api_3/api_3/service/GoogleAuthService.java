package com.api_3.api_3.service;

import com.api_3.api_3.config.GoogleConfig;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.model.entity.GoogleCalendarRef;
import com.api_3.api_3.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class GoogleAuthService {

    private final GoogleAuthorizationCodeFlow flow;
    private final GoogleConfig googleConfig;
    private final UserRepository userRepository;

    public GoogleAuthService(GoogleAuthorizationCodeFlow flow,
            GoogleConfig googleConfig,
            UserRepository userRepository) {
        this.flow = flow;
        this.googleConfig = googleConfig;
        this.userRepository = userRepository;
    }

    public String gerarUrlAutenticacao(String userUuid) {
        return flow.newAuthorizationUrl()
                .setRedirectUri(googleConfig.getRedirectUri())
                .setState(userUuid)
                .build();
    }

    public void salvarTokens(String userUuid, String code) throws Exception {
        GoogleTokenResponse response = flow
                .newTokenRequest(code)
                .setRedirectUri(googleConfig.getRedirectUri())
                .execute();

        User user = userRepository.findById(userUuid).orElseThrow();

        GoogleCalendarRef google = user.getGoogleCalendar();

        google.setConnected(true);
        google.setAccessToken(response.getAccessToken());
        google.setRefreshToken(response.getRefreshToken());
        Long expires = response.getExpiresInSeconds();
        google.setExpiresAt(Instant.now().plusSeconds(expires != null ? expires : 3600));

        user.setGoogleCalendar(google);

        userRepository.save(user);
    }

}
