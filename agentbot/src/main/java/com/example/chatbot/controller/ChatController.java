package com.example.chatbot.controller;

import com.example.chatbot.model.ChatSummary;
import com.example.chatbot.repository.ChatSummaryRepository;
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
    private final UserProfileRepository userProfileRepository;
    private final ChatSummaryRepository chatSummaryRepository; // ChatSummaryRepository 추가

    @Autowired
    public ChatController(RestTemplate restTemplate,
                          ObjectMapper objectMapper,
                          UserProfileRepository userProfileRepository,
                          ChatSummaryRepository chatSummaryRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.userProfileRepository = userProfileRepository;
        this.chatSummaryRepository = chatSummaryRepository; // 주입
    }

    @GetMapping("/chat")
    public String showChatPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 사용자 관련 대화 요약 조회
        List<ChatSummary> chatSummaries = chatSummaryRepository.findByUserId(user.getUserId());
        model.addAttribute("chatSummaries", chatSummaries);

        // 사용자 정보 추출
        UserProfile userProfile = userProfileRepository.findByProfileUserId(user.getUserId());
        if (userProfile != null) {
            Map<String, String> assetInfo = prepareAssetInfo(userProfile);
            session.setAttribute("userAssetInfo", assetInfo);
            sendAssetInfoToFastAPI(assetInfo);

            String initialGreeting = String.format(
                    "안녕하세요, %s님! 예금을 %s원, 적금을 %s원, 펀드상품을 %s원, 대출을 %s원 보유하고 계시네요. 총 자산은 %s원입니다. 어떤 도움을 드릴까요?",
                    user.getUsername(), assetInfo.get("deposit"), assetInfo.get("savings"),
                    assetInfo.get("fund"), assetInfo.get("debt"), assetInfo.get("totalAssets")
            );
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
        model.addAttribute("chatHistory", chatHistory);

        return "chat"; // 채팅 페이지로 이동
    }

    private Map<String, String> prepareAssetInfo(UserProfile userProfile) {
        double deposit = userProfile.getDepositHoldings() != null ? userProfile.getDepositHoldings() : 0;
        double savings = userProfile.getSavingsHoldings() != null ? userProfile.getSavingsHoldings() : 0;
        double fund = userProfile.getFundHoldings() != null ? userProfile.getFundHoldings() : 0;
        double debt = userProfile.getDebtAmount() != null ? userProfile.getDebtAmount() : 0;
        double totalAssets = deposit + savings + fund - debt;

        Map<String, String> assetInfo = new HashMap<>();
        assetInfo.put("deposit", formatNumber(deposit));
        assetInfo.put("savings", formatNumber(savings));
        assetInfo.put("fund", formatNumber(fund));
        assetInfo.put("debt", formatNumber(debt));
        assetInfo.put("totalAssets", formatNumber(totalAssets));
        return assetInfo;
    }

    private String formatNumber(double number) {
        return new DecimalFormat("#,###").format(number);
    }

    private void sendAssetInfoToFastAPI(Map<String, String> assetInfo) {
        String url = fastApiUrl + "/set_asset_info";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(assetInfo, headers);
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

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
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




