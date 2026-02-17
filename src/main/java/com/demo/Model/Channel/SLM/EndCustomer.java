package com.demo.Model.Channel.SLM;

import jakarta.persistence.*;

@Entity
@Table(name = "end_customer")
public class EndCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licenceKey;

    @Column(nullable = false)
    private String endCustomerName;

    // Constructeur vide
    public EndCustomer() {
    }

    // Constructeur complet
    public EndCustomer(String licenceKey, String endCustomerName) {
        this.licenceKey = licenceKey;
        this.endCustomerName = endCustomerName;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getLicenceKey() {
        return licenceKey;
    }

    public String getEndCustomerName() {
        return endCustomerName;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setLicenceKey(String licenceKey) {
        this.licenceKey = licenceKey;
    }

    public void setEndCustomerName(String endCustomerName) {
        this.endCustomerName = endCustomerName;
    }
}
