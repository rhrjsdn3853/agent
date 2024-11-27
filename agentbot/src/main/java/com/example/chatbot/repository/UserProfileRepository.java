package com.example.chatbot.repository;

import com.example.chatbot.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    /**
     * 사용자 ID로 사용자 프로필 검색
     */
    UserProfile findByProfileUserId(String profileUserId);

    /**
     * 특정 나이 범위에 속하는 사용자 프로필 목록 검색
     * @param startAge 시작 나이
     * @param endAge 끝 나이
     * @return 특정 나이 범위의 사용자 프로필 목록
     */
    List<UserProfile> findByAgeBetween(Integer startAge, Integer endAge);
}
