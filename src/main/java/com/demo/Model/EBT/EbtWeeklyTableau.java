package com.demo.Model.EBT;

import jakarta.persistence.*;

@Entity
@Table(name = "ebt_weekly_tableau")
public class EbtWeeklyTableau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String weekNumber; // ex: W40, W41

    @Column(nullable = false)
    private Double caWeekly; // Chiffre d'affaire

    @Column
    private String comment; // Commentaire

    @Column(nullable = false)
    private String quarter; // Q1, Q2, Q3, Q4

    // Constructeurs
    public EbtWeeklyTableau() {}

    public EbtWeeklyTableau(String weekNumber, Double caWeekly, String comment, String quarter) {
        this.weekNumber = weekNumber;
        this.caWeekly = caWeekly;
        this.comment = comment;
        this.quarter = quarter;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(String weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Double getCaWeekly() {
        return caWeekly;
    }

    public void setCaWeekly(Double caWeekly) {
        this.caWeekly = caWeekly;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }
}
