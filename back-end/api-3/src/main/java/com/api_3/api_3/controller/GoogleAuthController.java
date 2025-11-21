package com.api_3.api_3.controller;

import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.GoogleCalendarService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/google")
@CrossOrigin
public class GoogleAuthController {

    private final UserRepository userRepository;
    private final GoogleCalendarService calendarService;


    public GoogleAuthController(UserRepository userRepository, GoogleCalendarService calendarService) {
        this.userRepository = userRepository;
        this.calendarService = calendarService;
    }

    @PostMapping("/token")
    public Map<String, Object> saveToken(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String token = body.get("token");
        Map<String, Object> resp = new HashMap<>();
        userRepository.findById(userId).ifPresent(u -> {
            u.setGoogleAccessToken(token);
            userRepository.save(u);
        });
        resp.put("success", true);
        resp.put("savedAt", Instant.now().toString());
        return resp;
    }

    @GetMapping("/events")
    public String listEvents(@RequestParam String userId,
                             @RequestParam(required = false) String timeMin,
                             @RequestParam(required = false) String timeMax) throws Exception {
        return userRepository.findById(userId)
                .map(u -> {
                    try {
                        return calendarService.listEvents(u, timeMin, timeMax);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "[]";
                    }
                })
                .orElse("[]");
    }
}
