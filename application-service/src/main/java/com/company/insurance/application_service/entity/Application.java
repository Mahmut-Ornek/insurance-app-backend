package com.company.insurance.application_service.entity;


import com.company.insurance.application_service.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private ApplicationStatus status;

    @Column(nullable = false)
    private BigDecimal calculatedPrice;

    @Column(nullable = false)
    private String currencyCode = "TRY";

    private String description;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    private LocalDateTime decidedAt;

    private String decidedBy;

    @Column(nullable = false)
    private boolean isDeleted;

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public BigDecimal getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(BigDecimal calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(String decidedBy) {
        this.decidedBy = decidedBy;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
