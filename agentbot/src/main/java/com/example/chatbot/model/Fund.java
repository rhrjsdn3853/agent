package com.example.chatbot.model;

public class Fund {
    private String productName;  // 상품명
    private String productType;  // 상품 유형
    private String riskLevel;    // 위험도
    private String return1Month; // 1개월 수익률
    private String return3Months;// 3개월 수익률
    private String return6Months;// 6개월 수익률
    private String return12Months;// 12개월 수익률
    private String totalFee;     // 총 보수비용
    private String managementCompany; // 운용사
    private String productSummary; // 상품 요약
    private String cluster;       // 클러스터

    // Constructor
    public Fund(String productName, String productType, String riskLevel, String return1Month,
                String return3Months, String return6Months, String return12Months, String totalFee,
                String managementCompany, String productSummary, String cluster) {
        this.productName = productName;
        this.productType = productType;
        this.riskLevel = riskLevel;
        this.return1Month = return1Month;
        this.return3Months = return3Months;
        this.return6Months = return6Months;
        this.return12Months = return12Months;
        this.totalFee = totalFee;
        this.managementCompany = managementCompany;
        this.productSummary = productSummary;
        this.cluster = cluster;
    }

    // Getters and Setters
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getReturn1Month() { return return1Month; }
    public void setReturn1Month(String return1Month) { this.return1Month = return1Month; }

    public String getReturn3Months() { return return3Months; }
    public void setReturn3Months(String return3Months) { this.return3Months = return3Months; }

    public String getReturn6Months() { return return6Months; }
    public void setReturn6Months(String return6Months) { this.return6Months = return6Months; }

    public String getReturn12Months() { return return12Months; }
    public void setReturn12Months(String return12Months) { this.return12Months = return12Months; }

    public String getTotalFee() { return totalFee; }
    public void setTotalFee(String totalFee) { this.totalFee = totalFee; }

    public String getManagementCompany() { return managementCompany; }
    public void setManagementCompany(String managementCompany) { this.managementCompany = managementCompany; }

    public String getProductSummary() { return productSummary; }
    public void setProductSummary(String productSummary) { this.productSummary = productSummary; }

    public String getCluster() { return cluster; }
    public void setCluster(String cluster) { this.cluster = cluster; }
}
