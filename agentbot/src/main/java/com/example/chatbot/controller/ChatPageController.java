package com.example.chatbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/chatPage")
public class ChatPageController {

    @GetMapping
    public String showChatPage() {
        return "chat";  // src/main/resources/templates/chat.html을 렌더링
    }
}
