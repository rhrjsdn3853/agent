package com.example.chatbot.controller;

import com.example.chatbot.model.ChatSummary;
import com.example.chatbot.model.User;
import com.example.chatbot.repository.ChatSummaryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat-summary")
public class ChatSummaryController {

    @Autowired
    private ChatSummaryRepository chatSummaryRepository;

    @PostMapping("/create")
    public ResponseEntity<String> createChatSummary(HttpSession session, @RequestBody Map<String, String> requestData) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String title = requestData.get("title");
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("제목이 비어 있습니다.");
        }

        ChatSummary chatSummary = new ChatSummary();
        chatSummary.setUserId(user.getUserId());
        chatSummary.setTitle(title);
        chatSummary.setUserMessage(""); // 초기 메시지 없음
        chatSummary.setBotMessage("안녕하세요! 새 채팅을 시작했습니다. 무엇을 도와드릴까요?");

        try {
            chatSummaryRepository.save(chatSummary);
            return ResponseEntity.ok("새 채팅 생성 성공");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("새 채팅 생성 중 오류 발생");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatSummary> getChatSummary(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        ChatSummary chatSummary = chatSummaryRepository.findById(id).orElse(null);
        if (chatSummary == null || !chatSummary.getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        return ResponseEntity.ok(chatSummary);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveChatSummary(HttpSession session, @RequestBody Map<String, Object> summaryData) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String title = (String) summaryData.get("title");
        List<Map<String, String>> chatHistory = (List<Map<String, String>>) summaryData.get("chatHistory");

        if (title == null || chatHistory == null || chatHistory.isEmpty()) {
            return ResponseEntity.badRequest().body("title 또는 chatHistory가 비어 있습니다.");
        }

        StringBuilder userMessages = new StringBuilder();
        StringBuilder botMessages = new StringBuilder();

        for (Map<String, String> chat : chatHistory) {
            String userMessage = chat.getOrDefault("user", "");
            String botMessage = chat.getOrDefault("bot", "");
            userMessages.append(userMessage).append("\n");
            botMessages.append(botMessage).append("\n");
        }

        ChatSummary chatSummary = new ChatSummary();
        chatSummary.setUserId(user.getUserId());
        chatSummary.setTitle(title);
        chatSummary.setUserMessage(userMessages.toString());
        chatSummary.setBotMessage(botMessages.toString());

        try {
            chatSummaryRepository.save(chatSummary);
            return ResponseEntity.ok("대화 저장 성공");
        } catch (Exception e) {
            e.printStackTrace(); // 로그로 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("대화 저장 중 오류 발생");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<ChatSummary>> getChatSummaries(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<ChatSummary> summaries = chatSummaryRepository.findByUserId(user.getUserId());
        return ResponseEntity.ok(summaries);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteChatSummary(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            ChatSummary chatSummary = chatSummaryRepository.findById(id).orElse(null);
            if (chatSummary == null || !chatSummary.getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
            }

            chatSummaryRepository.deleteById(id);
            return ResponseEntity.ok("대화가 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("대화 삭제 중 오류가 발생했습니다.");
        }
    }
}
