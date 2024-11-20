package com.example.chatbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class ExchangeRateController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/exchange-rate")
    public String getExchangeRatePage(Model model) {
        String fastApiUrl = "http://localhost:8001/get_exchange_rate";

        try {
            // FastAPI에서 데이터를 가져옵니다.
            Map<String, Object> response = restTemplate.getForObject(fastApiUrl, Map.class);

            if (response != null && response.containsKey("rates")) {
                model.addAttribute("rates", response.get("rates"));
            } else {
                model.addAttribute("error", "환율 데이터를 가져올 수 없습니다.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "FastAPI 호출 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "exchange-rate"; // exchange-rate.html 템플릿 반환
    }
}
