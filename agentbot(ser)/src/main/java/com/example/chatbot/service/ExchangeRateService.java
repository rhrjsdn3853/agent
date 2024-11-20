package com.example.chatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExchangeRateService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getExchangeRates() {
        String apiUrl = "http://3.107.180.37/get_exchange_rate"; // FastAPI URL

        try {
            // FastAPI에서 JSON 데이터를 가져옵니다.
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("FastAPI에서 환율 데이터를 가져오는 데 실패했습니다: " + e.getMessage());
        }
    }
}
