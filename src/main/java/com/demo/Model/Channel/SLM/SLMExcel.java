package com.demo.Model.Channel.SLM;

import java.time.LocalDate;

/**
 * Classe pour mapper les données brutes du fichier Excel SLM
 * Contient TOUTES les colonnes du fichier Excel
 */
public class SLMExcel {

    private String division;
    private String firstChannel;
    private String secondChannel;
    private String licenceKey;
    private String modelName;
    private double quantiter;
    private String priceType;
    private String productType;
    private String type;
    private String licenceStatus;
    private String soNo;
    private String mdmName;
    private String ratedRequested;
    private LocalDate endDate;

    // Constructeur vide
    public SLMExcel() {
    }

    // Constructeur complet
    public SLMExcel(String division, String firstChannel, String secondChannel, String licenceKey,
                    String modelName, double quantiter, String priceType, String productType,
                    String type, String licenceStatus, String soNo, String mdmName,
                    String ratedRequested, LocalDate endDate) {
        this.division = division;
        this.firstChannel = firstChannel;
        this.secondChannel = secondChannel;
        this.licenceKey = licenceKey;
        this.modelName = modelName;
        this.quantiter = quantiter;
        this.priceType = priceType;
        this.productType = productType;
        this.type = type;
        this.licenceStatus = licenceStatus;
        this.soNo = soNo;
        this.mdmName = mdmName;
        this.ratedRequested = ratedRequested;
        this.endDate = endDate;
    }

    // Getters
    public String getDivision() {
        return division;
    }

    public String getFirstChannel() {
        return firstChannel;
    }

    public String getSecondChannel() {
        return secondChannel;
    }

    public String getLicenceKey() {
        return licenceKey;
    }

    public String getModelName() {
        return modelName;
    }

    public double getQuantiter() {
        return quantiter;
    }

    public String getPriceType() {
        return priceType;
    }

    public String getProductType() {
        return productType;
    }

    public String getType() {
        return type;
    }

    public String getLicenceStatus() {
        return licenceStatus;
    }

    public String getSoNo() {
        return soNo;
    }

    public String getMdmName() {
        return mdmName;
    }

    public String getRatedRequested() {
        return ratedRequested;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // Setters
    public void setDivision(String division) {
        this.division = division;
    }

    public void setFirstChannel(String firstChannel) {
        this.firstChannel = firstChannel;
    }

    public void setSecondChannel(String secondChannel) {
        this.secondChannel = secondChannel;
    }

    public void setLicenceKey(String licenceKey) {
        this.licenceKey = licenceKey;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setQuantiter(double quantiter) {
        this.quantiter = quantiter;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLicenceStatus(String licenceStatus) {
        this.licenceStatus = licenceStatus;
    }

    public void setSoNo(String soNo) {
        this.soNo = soNo;
    }

    public void setMdmName(String mdmName) {
        this.mdmName = mdmName;
    }

    public void setRatedRequested(String ratedRequested) {
        this.ratedRequested = ratedRequested;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
