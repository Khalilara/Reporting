// src/main/java/com/demo/dto/RevenueInsight.java
package com.demo.dto;

public class RevenueInsight {
    private String clientType;
    private double totalSLM;
    private double commonSLMPrepared;
    private double onlySLM;

    // Constructeurs
    public RevenueInsight() {}
    
    public RevenueInsight(String clientType, double totalSLM, double commonSLMPrepared, double onlySLM) {
        this.clientType = clientType;
        this.totalSLM = totalSLM;
        this.commonSLMPrepared = commonSLMPrepared;
        this.onlySLM = onlySLM;
    }

    // Getters/Setters
    public String getClientType() { return clientType; }
    public void setClientType(String clientType) { this.clientType = clientType; }
    
    public double getTotalSLM() { return totalSLM; }
    public void setTotalSLM(double totalSLM) { this.totalSLM = totalSLM; }
    
    public double getCommonSLMPrepared() { return commonSLMPrepared; }
    public void setCommonSLMPrepared(double commonSLMPrepared) { this.commonSLMPrepared = commonSLMPrepared; }
    
    public double getOnlySLM() { return onlySLM; }
    public void setOnlySLM(double onlySLM) { this.onlySLM = onlySLM; }
}
