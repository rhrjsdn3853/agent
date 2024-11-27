package com.example.chatbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private String profileUserId;

    @Column(name = "password")
    private String profilePassword;

    @Column(name = "gender")
    private String profileGender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "employment_status")
    private String employmentStatus;

    @Column(name = "monthly_income")
    private Double monthlyIncome;

    @Column(name = "savings_amount")
    private Double savingsAmount;

    @Column(name = "deposit_holdings")
    private Double depositHoldings;

    @Column(name = "savings_holdings")
    private Double savingsHoldings;

    @Column(name = "fund_holdings")
    private Double fundHoldings;

    @Column(name = "debt_amount")
    private Double debtAmount;

    @Column(name = "total_financial_assets")
    private Double totalFinancialAssets;

    @Column(name = "debt_to_equity_ratio")
    private Double debtToEquityRatio;

    @Column(name = "cluster")
    private Integer cluster;

    // Getters and Setters
    public String getProfileUserId() {
        return profileUserId;
    }

    public void setProfileUserId(String profileUserId) {
        this.profileUserId = profileUserId;
    }

    public String getProfilePassword() {
        return profilePassword;
    }

    public void setProfilePassword(String profilePassword) {
        this.profilePassword = profilePassword;
    }

    public String getProfileGender() {
        return profileGender;
    }

    public void setProfileGender(String profileGender) {
        this.profileGender = profileGender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Double getSavingsAmount() {
        return savingsAmount;
    }

    public void setSavingsAmount(Double savingsAmount) {
        this.savingsAmount = savingsAmount;
    }

    public Double getDepositHoldings() {
        return depositHoldings;
    }

    public void setDepositHoldings(Double depositHoldings) {
        this.depositHoldings = depositHoldings;
    }

    public Double getSavingsHoldings() {
        return savingsHoldings;
    }

    public void setSavingsHoldings(Double savingsHoldings) {
        this.savingsHoldings = savingsHoldings;
    }

    public Double getFundHoldings() {
        return fundHoldings;
    }

    public void setFundHoldings(Double fundHoldings) {
        this.fundHoldings = fundHoldings;
    }

    public Double getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(Double debtAmount) {
        this.debtAmount = debtAmount;
    }

    public Double getTotalFinancialAssets() {
        return totalFinancialAssets;
    }

    public void setTotalFinancialAssets(Double totalFinancialAssets) {
        this.totalFinancialAssets = totalFinancialAssets;
    }


    public Double getDebtToEquityRatio() {
        return debtToEquityRatio;
    }

    public void setDebtToEquityRatio(Double debtToEquityRatio) {
        this.debtToEquityRatio = debtToEquityRatio;
    }

    public Integer getCluster() {
        return cluster;
    }

    public void setCluster(Integer cluster) {
        this.cluster = cluster;
    }
}
