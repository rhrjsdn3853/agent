package com.example.chatbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class UserForeignCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Column(nullable = false)
    private Double totalSpent = 0.0;

    @Column(nullable = false)
    private Double totalConvertedKrw = 0.0;

    // Default constructor (required by JPA)
    public UserForeignCurrency() {
    }

    // All-args constructor
    public UserForeignCurrency(Long id, String userId, String currency, Double balance, Double totalSpent, Double totalConvertedKrw) {
        this.id = id;
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.totalSpent = totalSpent;
        this.totalConvertedKrw = totalConvertedKrw;
    }

    // Constructor with selected fields
    public UserForeignCurrency(String userId, String currency, Double balance, Double totalSpent, Double totalConvertedKrw) {
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.totalSpent = totalSpent;
        this.totalConvertedKrw = totalConvertedKrw;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Double getTotalConvertedKrw() {
        return totalConvertedKrw;
    }

    public void setTotalConvertedKrw(Double totalConvertedKrw) {
        this.totalConvertedKrw = totalConvertedKrw;
    }
}
