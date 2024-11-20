package com.example.chatbot.service;

import com.example.chatbot.model.Notification;
import com.example.chatbot.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * 특정 사용자의 알림 목록을 반환
     * @param userId 사용자 ID
     * @return 알림 목록 (최신순 정렬)
     */
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 특정 알림 읽음 처리
     * @param id 알림 ID
     */
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    /**
     * 새로운 알림 생성
     * @param notification 알림 객체
     * @return 저장된 알림
     */
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * 특정 알림 삭제
     * @param id 알림 ID
     */
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found");
        }
        notificationRepository.deleteById(id);
    }
}
