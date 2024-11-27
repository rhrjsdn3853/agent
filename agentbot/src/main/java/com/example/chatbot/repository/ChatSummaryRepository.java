package com.example.chatbot.repository;

import com.example.chatbot.model.ChatSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSummaryRepository extends JpaRepository<ChatSummary, Long> {
    List<ChatSummary> findByUserId(String userId);
}
