package com.example.chatbot.controller;

import com.example.chatbot.dto.TargetRateRequest;
import com.example.chatbot.model.UserForeignCurrency;
import com.example.chatbot.service.ForeignCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/foreign-currency")
public class ForeignCurrencyController {

    @Autowired
    private ForeignCurrencyService foreignCurrencyService;

    // 사용자 외화 보유 정보 조회
    @GetMapping("/{userId}/holdings")
    public ResponseEntity<List<UserForeignCurrency>> getForeignHoldings(@PathVariable String userId) {
        List<UserForeignCurrency> holdings = foreignCurrencyService.getUserForeignHoldings(userId);
        return ResponseEntity.ok(holdings);
    }

    // 목표 설정 시 구매/판매 처리
    @PostMapping("/set-target")
    public ResponseEntity<String> setTargetRate(@RequestBody TargetRateRequest request) {
        try {
            String result = foreignCurrencyService.processTransaction(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

