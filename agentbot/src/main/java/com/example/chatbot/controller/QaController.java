package com.example.chatbot.controller;

import com.example.chatbot.model.Question;
import com.example.chatbot.model.User;
import com.example.chatbot.repository.QuestionRepository;
import com.example.chatbot.service.QuestionService; // QuestionService 추가
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Page 추가
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // RequestParam 추가

@Controller
public class QaController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionService questionService; // QuestionService 주입

    @GetMapping("/qa")
    public String showQnaPage(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Question> questions = questionService.getQuestions(page, 10); // 한 페이지에 10개 질문
        model.addAttribute("questions", questions);
        return "qa"; // qa.html
    }

    @GetMapping("/qa/new")
    public String showNewQuestionForm(HttpSession session) {
        // 로그인 확인
        if (session.getAttribute("user") == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
        return "new-question"; // new-question.html
    }

    @GetMapping("/qa/{id}")
    public String viewQuestion(@PathVariable Long id, Model model, HttpSession session) {
        System.out.println("Fetching question with ID: " + id); // 디버깅용 로그
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));
        User user = (User) session.getAttribute("user");
        model.addAttribute("question", question);
        model.addAttribute("currentUser", user);
        return "question-details";
    }


    @PostMapping("/qa/submit")
    public String addQuestion(Question question, HttpSession session) {
        User user = (User) session.getAttribute("user"); // 세션에서 User 객체 가져오기
        if (user != null) {
            question.setUsername(user.getUsername()); // 작성자 이름 자동 설정
            questionRepository.save(question); // 질문 저장
            return "redirect:/qa"; // Q&A 페이지로 리다이렉트
        }
        return "redirect:/login"; // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
    }

    @GetMapping("/qa/edit/{id}")
    public String showEditQuestionForm(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));

        // 작성자와 현재 사용자 확인
        if (!question.getUsername().equals(user.getUsername())) {
            return "redirect:/qa"; // 권한이 없는 경우 Q&A 페이지로 리다이렉트
        }

        model.addAttribute("question", question);
        return "edit-question"; // edit-question.html
    }

    @PostMapping("/qa/update/{id}")
    public String updateQuestion(@PathVariable Long id, Question updatedQuestion, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));

        // 작성자 확인
        if (!question.getUsername().equals(user.getUsername())) {
            return "redirect:/qa"; // 권한이 없는 경우 Q&A 페이지로 리다이렉트
        }

        // 수정된 내용으로 업데이트
        question.setTitle(updatedQuestion.getTitle());
        question.setContent(updatedQuestion.getContent());
        questionRepository.save(question); // 질문 저장

        return "redirect:/qa"; // Q&A 페이지로 리다이렉트
    }

    @GetMapping("/qa/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + id));

        // 작성자 확인
        if (!question.getUsername().equals(user.getUsername())) {
            return "redirect:/qa"; // 권한이 없는 경우 Q&A 페이지로 리다이렉트
        }

        questionRepository.delete(question); // 질문 삭제
        return "redirect:/qa"; // Q&A 페이지로 리다이렉트
    }
}
