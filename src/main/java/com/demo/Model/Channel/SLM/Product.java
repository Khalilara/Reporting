package com.demo.Model.Channel.SLM;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String modelName;

    @Column(nullable = false)
    private double prixUnitaire;

    private String description;

    // Constructeur vide
    public Product() {
    }

    // Constructeur complet
    public Product(String modelName, double prixUnitaire, String description) {
        this.modelName = modelName;
        this.prixUnitaire = prixUnitaire;
        this.description = description;
    }

    // Constructeur sans description
    public Product(String modelName, double prixUnitaire) {
        this.modelName = modelName;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getModelName() {
        return modelName;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
