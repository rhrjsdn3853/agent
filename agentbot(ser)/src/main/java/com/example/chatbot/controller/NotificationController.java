package com.example.chatbot.controller;

import com.example.chatbot.model.Notification;
import com.example.chatbot.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 특정 사용자의 알림 목록을 JSON으로 반환
     * @param session 세션에서 사용자 ID를 가져옴
     * @return 알림 목록
     */
    @GetMapping("/list")
    @ResponseBody
    public List<Notification> getNotifications(HttpSession session) {
        String userId = (String) session.getAttribute("user_id"); // 세션에서 user_id 가져오기
        if (userId == null) {
            throw new RuntimeException("로그인되지 않은 사용자입니다."); // 로그인되지 않은 경우 예외 발생
        }
        return notificationService.getUserNotifications(userId);
    }

    /**
     * 특정 알림 읽음 처리
     * @param id 알림 ID
     * @return 성공 메시지
     */
    @PostMapping("/mark-as-read/{id}")
    @ResponseBody
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "Notification marked as read.";
    }

    /**
     * 알림 페이지로 이동
     * @param session 세션에서 사용자 ID를 가져옴
     * @param model 알림 데이터를 전달할 모델
     * @return 알림 페이지 경로
     */
    @GetMapping
    public String showNotificationsPage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("user_id"); // 세션에서 user_id 가져오기
        if (userId == null) {
            // 로그인되지 않은 사용자는 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }

        List<Notification> notifications = notificationService.getUserNotifications(userId);
        model.addAttribute("notifications", notifications);
        model.addAttribute("userId", userId); // 사용자 ID를 모델에 추가
        return "notifications"; // notifications.html 렌더링
    }

    /**
     * 새로운 알림 생성
     * @param notification 생성할 알림
     * @return 저장된 알림 정보
     */
    @PostMapping("/create")
    @ResponseBody
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.saveNotification(notification);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return "Notification deleted successfully.";
    }

}
