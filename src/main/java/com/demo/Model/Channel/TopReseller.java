package com.demo.Model.Channel;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "top_reseller")
public class TopReseller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private BigDecimal target;
    public TopReseller() {}
    public TopReseller(String name, double target) {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }
}
