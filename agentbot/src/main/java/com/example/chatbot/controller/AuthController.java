package com.example.chatbot.controller;

import com.example.chatbot.model.User;
import com.example.chatbot.model.UserProfile;
import com.example.chatbot.repository.UserRepository;
import com.example.chatbot.repository.UserProfileRepository;
import com.example.chatbot.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String userId,
                         @RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String phone,
                         @RequestParam String ssn,
                         @RequestParam String gender,
                         @RequestParam String email,
                         @RequestParam String securityQuestion,
                         @RequestParam String securityAnswer,
                         Model model) {
        if (userId.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty() || ssn.isEmpty() ||
                gender.isEmpty() || email.isEmpty() || securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
            model.addAttribute("error", "모든 필드를 올바르게 입력해 주세요.");
            return "signup";
        }

        User user = new User(userId, username, password, phone, ssn, gender, email, securityQuestion, securityAnswer);
        userRepository.save(user);
        model.addAttribute("message", "회원가입이 완료되었습니다.");
        return "signup-success";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String userId,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        User user = userService.findByUserId(userId);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);  // 세션에 User 객체 저장
            session.setAttribute("user_id", user.getUserId());  // 세션에 user_id 저장

            // 로그인한 사용자의 프로필 정보도 세션에 저장
            UserProfile userProfile = userProfileRepository.findByProfileUserId(user.getUserId());
            if (userProfile != null) {
                session.setAttribute("userProfile", userProfile);  // 세션에 UserProfile 저장
                // 자산 정보만 따로 저장해서 챗봇에서 사용할 수 있게 설정
                Map<String, String> userAssetInfo = new HashMap<>();
                userAssetInfo.put("deposit", String.valueOf(userProfile.getDepositHoldings()));
                userAssetInfo.put("savings", String.valueOf(userProfile.getSavingsHoldings()));
                userAssetInfo.put("fund", String.valueOf(userProfile.getFundHoldings()));
                session.setAttribute("userAssetInfo", userAssetInfo);  // 세션에 자산 정보 저장
            }

            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  // 세션 무효화
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String showProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String phone,
                                @RequestParam String email,
                                HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            user.setPhone(phone);
            user.setEmail(email);
            userRepository.save(user);
            session.setAttribute("user", user);  // 업데이트된 정보를 세션에 저장
        }
        return "redirect:/profile";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            userRepository.delete(user);
            session.invalidate();
        }
        return "redirect:/";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (!user.getPassword().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 맞지 않습니다.");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile";
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다.");
        return "redirect:/profile";
    }

    @GetMapping("/find-password")
    public String showFindPasswordPage() {
        return "find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam String userId,
                               @RequestParam String securityQuestion,
                               @RequestParam String securityAnswer,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "존재하지 않는 아이디입니다.");
            return "redirect:/find-password";
        }

        if (!userService.securityQuestionIsValid(user, securityQuestion, securityAnswer)) {
            redirectAttributes.addFlashAttribute("error", "올바르지 않은 질문 또는 답변입니다.");
            return "redirect:/find-password";
        }

        // 성공적으로 검증되면 세션에 userId를 저장
        session.setAttribute("resetUserId", userId);
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("resetUserId");

        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "올바르지 않은 접근입니다.");
            return "redirect:/find-password";
        }

        model.addAttribute("userId", userId);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("resetUserId");

        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "올바르지 않은 접근입니다.");
            return "redirect:/find-password";
        }

        User user = userRepository.findByUserId(userId);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "존재하지 않는 아이디입니다.");
            return "redirect:/reset-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/reset-password";
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        // 비밀번호 변경 후 세션에서 userId 제거
        session.removeAttribute("resetUserId");

        redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다.");
        return "redirect:/login";
    }
}
