package com.example.chatbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.chatbot.model.Notification;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
}
