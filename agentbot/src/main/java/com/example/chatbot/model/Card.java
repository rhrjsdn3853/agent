package com.example.chatbot.model;

public class Card {
    private String cardName;
    private String cardType;
    private String annualFee;
    private String details;

    public Card(String cardName, String cardType, String annualFee, String details) {
        this.cardName = cardName;
        this.cardType = cardType;
        this.annualFee = annualFee;
        this.details = details;
    }

    // Getters and Setters
    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(String annualFee) {
        this.annualFee = annualFee;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
