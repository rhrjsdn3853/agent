package com.example.chatbot.repository;

import com.example.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId); // userId로 사용자 찾기

    // 이메일로 사용자 찾기
    User findByEmail(String email);

    // 보안 질문과 답변으로 사용자 찾기 (아이디와 이메일이 일치하는 경우)
    User findByUserIdAndEmailAndSecurityAnswer(String userId, String email, String securityAnswer);
}
