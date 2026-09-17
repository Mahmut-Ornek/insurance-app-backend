package com.company.insurance.parameter_service.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rate")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private LocalDate rateDate;

    @Column(nullable = false)
    private BigDecimal forexSelling;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ExchangeRate(){}

    public ExchangeRate(String currencyCode, LocalDate rateDate, BigDecimal forexSelling){
        this.currencyCode = currencyCode;
        this.rateDate = rateDate;
        this.forexSelling = forexSelling;
    }

    public Long getId() { return id; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public LocalDate getRateDate() { return rateDate; }
    public void setRateDate(LocalDate rateDate) { this.rateDate = rateDate; }
    public BigDecimal getForexSelling() { return forexSelling; }
    public void setForexSelling(BigDecimal forexSelling) { this.forexSelling = forexSelling; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
