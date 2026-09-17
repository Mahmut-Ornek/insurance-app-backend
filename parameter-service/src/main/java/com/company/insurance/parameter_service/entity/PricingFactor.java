package com.company.insurance.parameter_service.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_factor")
public class PricingFactor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String factorType;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    @Column(nullable = false)
    private BigDecimal multiplier;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFactorType() {
        return factorType;
    }

    public void setFactorType(String factorType) {
        this.factorType = factorType;
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public BigDecimal getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
