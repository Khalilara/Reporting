package com.demo.Model.EBT;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evolution_ebt")
public class EvolutionEBT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String grossiste;

    @Column
    private String revendeur;

    @Column
    private String client;

    @Column
    private String solution;

    @Column
    private String clefDeLicence;

    @Column
    private double quantite;

    @Column
    private double prix;

    @Column
    private double CaAttendu;

    @Column
    private double CaVendu;

    @Column
    private LocalDate dateDeDebut;

    @Column
    private LocalDate dateDeFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    private StatusEvo status;

    @Column
    private String proba;

    @Column
    private String commentaire;

    @Column
    private String quarter;

    @Column
    private Double targetQuarter;


    // Constructeurs
    public EvolutionEBT() {}

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrossiste() {
        return grossiste;
    }

    public void setGrossiste(String grossiste) {
        this.grossiste = grossiste;
    }

    public String getRevendeur() {
        return revendeur;
    }

    public void setRevendeur(String revendeur) {
        this.revendeur = revendeur;
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

    public String getClefDeLicence() {
        return clefDeLicence;
    }

    public void setClefDeLicence(String clefDeLicence) {
        this.clefDeLicence = clefDeLicence;
    }

    public double getQuantite() {
        return quantite;
    }
    public String getQuarter() {
        return quarter;
    }
    public void setQuarter(String quarter) {
        this.quarter = quarter;
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

    public double getCaAttendu() {
        return CaAttendu;
    }

    public void setCaAttendu(double CaAttendu) {
        this.CaAttendu = CaAttendu;
    }

    public double getCaVendu() {
        return CaVendu;
    }

    public void setCaVendu(double CaVendu) {
        this.CaVendu = CaVendu;
    }

    public LocalDate getDateDeDebut() {
        return dateDeDebut;
    }

    public void setDateDeDebut(LocalDate dateDeDebut) {
        this.dateDeDebut = dateDeDebut;
    }

    public LocalDate getDateDeFin() {
        return dateDeFin;
    }

    public void setDateDeFin(LocalDate dateDeFin) {
        this.dateDeFin = dateDeFin;
    }

    public StatusEvo getStatus() {
        return status;
    }

    public void setStatus(StatusEvo status) {
        this.status = status;
    }

    public String getProba() {
        return proba;
    }

    public void setProba(String proba) {
        this.proba = proba;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Double getTargetQuarter() {
        return targetQuarter;
    }

    public void setTargetQuarter(Double targetQuarter) {
        this.targetQuarter = targetQuarter;
    }

    @Override
    public String toString() {
        return "EvolutionEBT{" +
                "id=" + id +
                ", grossiste='" + grossiste + '\'' +
                ", revendeur='" + revendeur + '\'' +
                ", client='" + client + '\'' +
                ", solution='" + solution + '\'' +
                ", clefDeLicence='" + clefDeLicence + '\'' +
                ", quantite=" + quantite +
                ", prix=" + prix +
                ", CaAttendu=" + CaAttendu +
                ", CaVendu=" + CaVendu +
                ", dateDeDebut=" + dateDeDebut +
                ", dateDeFin=" + dateDeFin +
                ", status=" + status +
                ", proba='" + proba + '\'' +
                ", commentaire='" + commentaire + '\'' +
                '}';
    }
}