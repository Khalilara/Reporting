package com.demo.Model.EBT;

import jakarta.persistence.*;

@Entity
@Table(name = "tableau_ebt")
public class TableauEBT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type;

    @Column
    private String probabilite;

    @Column
    private String client;

    @Column
    private String solution;

    @Column
    private double quantite;

    @Column
    private double prix;

    @Column
    private double chiffreAffaire;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private Status status;

    @Column
    private String kam;

    @Column
    private String info;

    @Column
    private String quarter;

    // Constructeurs
    public TableauEBT() {}

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProbabilite() {
        return probabilite;
    }

    public void setProbabilite(String probabilite) {
        this.probabilite = probabilite;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public double getChiffreAffaire() {
        return chiffreAffaire;
    }

    public void setChiffreAffaire(double chiffreAffaire) {
        this.chiffreAffaire = chiffreAffaire;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getKam() {
        return kam;
    }

    public void setKam(String kam) {
        this.kam = kam;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    @Override
    public String toString() {
        return "TableauEBT{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", probabilite='" + probabilite + '\'' +
                ", client='" + client + '\'' +
                ", solution='" + solution + '\'' +
                ", quantite=" + quantite +
                ", prix=" + prix +
                ", chiffreAffaire=" + chiffreAffaire +
                ", status=" + status +
                ", kam='" + kam + '\'' +
                ", info='" + info + '\'' +
                ", quarter='" + quarter + '\'' +
                '}';
    }
}