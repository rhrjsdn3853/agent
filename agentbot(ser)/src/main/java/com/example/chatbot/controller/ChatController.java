package com.example.chatbot.controller;

import com.example.chatbot.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.chatbot.model.User;
import com.example.chatbot.model.UserProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    @Value("${fastapi.url}")
    private String fastApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;  // UserProfileRepository 주입

    public ChatController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/chat")
    public String showChatPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 사용자 정보 추출
        UserProfile userProfile = userProfileRepository.findByProfileUserId(user.getUserId());
        if (userProfile != null) {
            // 자산 정보 세션에 저장 (10000배 곱하기)
            Map<String, String> assetInfo = new HashMap<>();
            double deposit = userProfile.getDepositHoldings() != null ? userProfile.getDepositHoldings() * 10000 : 0;
            double savings = userProfile.getSavingsHoldings() != null ? userProfile.getSavingsHoldings() * 10000 : 0;
            double fund = userProfile.getFundHoldings() != null ? userProfile.getFundHoldings() * 10000 : 0;
            double debt = userProfile.getDebtAmount() != null ? userProfile.getDebtAmount() * 10000 : 0;

            // 총 자산 계산 (예금 + 적금 + 펀드 + 부채)
            double totalAssets = deposit + savings + fund + debt;

            assetInfo.put("deposit", formatNumber(deposit));
            assetInfo.put("savings", formatNumber(savings));
            assetInfo.put("fund", formatNumber(fund));
            assetInfo.put("debt", formatNumber(debt));
            assetInfo.put("totalAssets", formatNumber(totalAssets)); // 총 자산 추가

            // 자산 정보를 세션에 저장
            session.setAttribute("userAssetInfo", assetInfo);

            // FastAPI로 자산 정보 전달
            sendAssetInfoToFastAPI(assetInfo);

            // 초기 인사말 메시지 생성
            String initialGreeting = String.format(
                    "안녕하세요, %s님! 예금을 %s원, 적금을 %s원, 펀드상품을 %s원, 대출을 %s원 보유하고 계시네요. 총 자산은 %s원입니다. 어떤 도움을 드릴까요?",
                    user.getUsername(), assetInfo.get("deposit"), assetInfo.get("savings"), assetInfo.get("fund"),
                    assetInfo.get("debt"), assetInfo.get("totalAssets")
            );

            model.addAttribute("user", user);
            model.addAttribute("initialGreeting", initialGreeting);
        } else {
            model.addAttribute("initialGreeting", "사용자의 자산 정보가 없습니다.");
        }

        // 세션에서 이전 채팅 내역 가져오기
        List<String> chatHistory = (List<String>) session.getAttribute("chatHistory");
        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
            session.setAttribute("chatHistory", chatHistory);
        }
        model.addAttribute("chatHistory", chatHistory); // 이전 채팅 내역을 모델에 추가하여 뷰에서 표시

        return "chat"; // 채팅 페이지로 이동
    }

    // 숫자를 '원' 단위로 포맷팅하는 메서드
    private String formatNumber(double number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);  // 천 단위 구분 쉼표 추가
    }

    // 자산 정보를 FastAPI로 전달하는 메서드
    private void sendAssetInfoToFastAPI(Map<String, String> assetInfo) {
        String url = fastApiUrl + "/set_asset_info";  // FastAPI의 자산 정보 설정 엔드포인트
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(assetInfo, headers);

        // FastAPI 서버로 자산 정보 전달
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> userInput, HttpSession session) {
        String userMessage = userInput.get("message");

        // 사용자의 자산 정보를 세션에서 가져오기
        Map<String, String> assetInfo = (Map<String, String>) session.getAttribute("userAssetInfo");

        // FastAPI로 자산 정보와 함께 질문 전달
        String url = fastApiUrl + "/recommend_savings";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("question", userMessage);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
            String botResponse = jsonNode.path("response").asText();

            // 채팅 내역에 추가
            List<String> chatHistory = (List<String>) session.getAttribute("chatHistory");
            if (chatHistory == null) {
                chatHistory = new ArrayList<>();
            }
            chatHistory.add("User: " + userMessage);
            chatHistory.add("Bot: " + botResponse);
            session.setAttribute("chatHistory", chatHistory);

            Map<String, String> response = new HashMap<>();
            response.put("response", botResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("response", "서버에서 응답을 받는 중에 오류가 발생했습니다."));
        }
    }
}
