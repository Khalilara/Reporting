package com.demo.Model.Channel.SLM;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="slm")
public class SLM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstChannel;
    private String secondChannel;
    private String endCustomer;
    private String title;
    private String licenceKey;
    private String modelName;
    private double quantiter;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private Status licenceStatus;
    private LocalDate endDate;
    private double prixUnitaire;
    private double totalPrice;
    private String clientType;

    // Constructeur vide
    public SLM() {
    }

    // Constructeur complet
    public SLM(String firstChannel, String secondChannel, String endCustomer, String title,
               String licenceKey, String modelName, double quantiter, Status licenceStatus,
               LocalDate endDate, double prixUnitaire, double totalPrice, String clientType) {
        this.firstChannel = firstChannel;
        this.secondChannel = secondChannel;
        this.endCustomer = endCustomer;
        this.title = title;
        this.licenceKey = licenceKey;
        this.modelName = modelName;
        this.quantiter = quantiter;
        this.licenceStatus = licenceStatus;
        this.endDate = endDate;
        this.prixUnitaire = prixUnitaire;
        this.totalPrice = totalPrice;
        this.clientType = clientType;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFirstChannel() {
        return firstChannel;
    }

    public String getSecondChannel() {
        return secondChannel;
    }

    public String getEndCustomer() {
        return endCustomer;
    }

    public String getTitle() {
        return title;
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

    public Status getLicenceStatus() {
        return licenceStatus;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getClientType() {
        return clientType;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstChannel(String firstChannel) {
        this.firstChannel = firstChannel;
    }

    public void setSecondChannel(String secondChannel) {
        this.secondChannel = secondChannel;
    }

    public void setEndCustomer(String endCustomer) {
        this.endCustomer = endCustomer;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setLicenceStatus(Status licenceStatus) {
        this.licenceStatus = licenceStatus;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
}
