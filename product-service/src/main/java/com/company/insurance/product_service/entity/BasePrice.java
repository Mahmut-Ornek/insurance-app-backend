package com.company.insurance.product_service.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "base_price")
public class BasePrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private Short month;

    @Column(nullable = false)
    private Short year;

    @Column(nullable = false)
    private boolean isDeleted;

    @Column(nullable = true)
    private String createdBy;

    @Column(nullable = true)
    private String updatedBy;

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Short getMonth() {
        return month;
    }

    public void setMonth(Short month) {
        this.month = month;
    }

    public Short getYear() {
        return year;
    }

    public void setYear(Short year) {
        this.year = year;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
