package com.example.chatbot.dto;


public class TargetRateRequest {
    private String userId;      // 사용자 ID
    private String currency;    // 외화 종류 (USD, EUR 등)
    private String action;      // 거래 유형 ("buy" 또는 "sell")
    private double targetRate;  // 목표 환율
    private double amount;      // 거래 금액

    // 기본 생성자
    public TargetRateRequest() {
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getTargetRate() {
        return targetRate;
    }

    public void setTargetRate(double targetRate) {
        this.targetRate = targetRate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
