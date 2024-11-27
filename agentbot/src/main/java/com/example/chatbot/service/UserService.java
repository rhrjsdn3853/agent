package com.example.chatbot.service;

import com.example.chatbot.model.User;
import com.example.chatbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public boolean validateUser(String userId, String password) { // username -> userId로 변경
        User user = findByUserId(userId); // userId로 사용자 찾기
        return user != null && user.getPassword().equals(password); // 비밀번호 검증
    }

    public boolean securityQuestionIsValid(User user, String securityQuestion, String securityAnswer) {
        return user.getSecurityQuestion().equals(securityQuestion) &&
                user.getSecurityAnswer().equals(securityAnswer);
    }
}
