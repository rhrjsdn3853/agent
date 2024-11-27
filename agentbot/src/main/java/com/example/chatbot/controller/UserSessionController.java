package com.example.chatbot.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserSessionController {

    @GetMapping("/session")
    public ResponseEntity<Map<String, String>> getSessionUser(HttpSession session) {
        String userId = (String) session.getAttribute("user_id");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인되지 않았습니다."));
        }

        return ResponseEntity.ok(Map.of("userId", userId));
    }
}
