package com.example.chatbot.repository;

import com.example.chatbot.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    UserProfile findByProfileUserId(String profileUserId);
}
