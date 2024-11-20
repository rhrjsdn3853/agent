//package com.example.chatbot;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import com.fasterxml.jackson.databind.JsonNode;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class    OpenAIService {
//
//    @Value("${openai.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//
//    public OpenAIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
//        this.restTemplate = restTemplate;
//        this.objectMapper = objectMapper;
//    }
//
//    public String getChatResponse(String message) {
//        String apiUrl = "https://api.openai.com/v1/chat/completions";
//
//        // 요청 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        // 요청 바디 준비
//        Map<String, Object> messageContent = new HashMap<>();
//        messageContent.put("role", "user");
//        messageContent.put("content", message);
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("model", "gpt-3.5-turbo");
//        body.put("messages", List.of(messageContent));
//
//        // 바디를 JSON 문자열로 변환
//        String jsonBody;
//        try {
//            jsonBody = objectMapper.writeValueAsString(body);
//        } catch (Exception e) {
//            throw new RuntimeException("요청 바디를 JSON으로 변환하는 데 실패했습니다", e);
//        }
//
//        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
//
//        // API 호출 및 응답 파싱
//        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
//        String responseBody = responseEntity.getBody();
//
//        // JSON 응답을 파싱하여 content 추출
//        try {
//            JsonNode jsonNode = objectMapper.readTree(responseBody);
//            return jsonNode.path("choices").get(0).path("message").path("content").asText();
//        } catch (Exception e) {
//            throw new RuntimeException("실패", e);
//        }
//    }
//}

//------------------------------

//public String getChatResponse(String message) {
//    String apiUrl = "https://api.openai.com/v1/chat/completions";
//
//    // 요청 헤더 설정
//    HttpHeaders headers = new HttpHeaders();
//    headers.setContentType(MediaType.APPLICATION_JSON);
//    headers.setBearerAuth(apiKey);
//
//    // 요청 바디 설정
//    Map<String, Object> messageContent = new HashMap<>();
//    messageContent.put("role", "user");
//    messageContent.put("content", message);
//
//    Map<String, Object> body = new HashMap<>();
//    body.put("model", "gpt-3.5-turbo");
//    body.put("messages", List.of(messageContent));
//
//    // 바디를 JSON 문자열로 변환
//    String jsonBody;
//    try {
//        jsonBody = objectMapper.writeValueAsString(body);
//    } catch (Exception e) {
//        throw new RuntimeException("Failed to convert request body to JSON", e);
//    }
//
//    HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
//
//    // API 호출 및 응답 로깅
//    try {
//        var responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
//        System.out.println("Status Code: " + responseEntity.getStatusCode());
//        System.out.println("Response Body: " + responseEntity.getBody());
//        return responseEntity.getBody();
//    } catch (Exception e) {
//        System.err.println("Error during API call: " + e.getMessage());
//        throw new RuntimeException("API request failed", e);
//    }
//}
