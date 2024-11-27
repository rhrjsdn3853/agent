package com.example.chatbot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class PythonModelService {

    public String getUserCluster(String userId) {
        String apiUrl = "http://localhost:8001/analyze-user";  // 엔드포인트에 "/analyze-user" 추가
        RestTemplate restTemplate = new RestTemplate();

        // 요청할 JSON 데이터 설정
        JSONObject requestJson = new JSONObject();
        requestJson.put("user_Id", userId);  // 'user_Id'로 수정

        // FastAPI로 POST 요청을 보내고 응답을 받습니다.
        String response = restTemplate.postForObject(apiUrl, requestJson.toString(), String.class);
        JSONObject responseJson = new JSONObject(response);

        return responseJson.getString("userType");  // FastAPI로부터 받은 클러스터 유형 반환
    }
}
