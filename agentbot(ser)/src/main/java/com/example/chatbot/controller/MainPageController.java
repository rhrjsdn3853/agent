package com.example.chatbot.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/")
    public String showMainPage() {
        return "main"; // 메인 페이지 렌더링
    }

    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "loginPage"; // 로그인 페이지 렌더링
    }

    @GetMapping("/signupPage")
    public String showSignupPage() {
        return "signupPage"; // 회원가입 페이지 렌더링
    }

    @GetMapping("/assets")
    public String showAssetsPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }
        return "assets"; // 로그인한 경우 자산 현황 페이지 렌더링
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about"; // about.html 페이지 렌더링
    }


}
