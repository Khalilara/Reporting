package com.demo.Model.EBT;

import jakarta.persistence.*;

@Entity
@Table(name = "quarter_summary")
public class QuarterSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String quarter; // Q1, Q2, Q3, Q4

    @Column
    private Double targetAmount;

    // Constructeurs
    public QuarterSummary() {}

    public QuarterSummary(String quarter, Double targetAmount) {
        this.quarter = quarter;
        this.targetAmount = targetAmount;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public Double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    @Override
    public String toString() {
        return "QuarterSummary{" +
                "id=" + id +
                ", quarter='" + quarter + '\'' +
                ", targetAmount=" + targetAmount +
                '}';
    }
}
