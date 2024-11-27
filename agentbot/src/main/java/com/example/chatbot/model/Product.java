package com.example.chatbot.model;

public class Product {
    private String title;
    private String bank;
    private String maxRate;
    private String baseRate;
    private String details;

    public Product(String title, String bank, String maxRate, String baseRate, String details) {
        this.title = title;
        this.bank = bank;
        this.maxRate = maxRate;
        this.baseRate = baseRate;
        this.details = details;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(String maxRate) {
        this.maxRate = maxRate;
    }

    public String getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(String baseRate) {
        this.baseRate = baseRate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
