package com.company.insurance.payment_service.entity;

import com.company.insurance.payment_service.converter.PaymentStatusConverter;
import com.company.insurance.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long collectionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currencyCode = "TRY";

    @Convert(converter = PaymentStatusConverter.class)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, unique = true)
    private String conversationId;

    private String token;

    private String iyzicoPaymentId;

    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIyzicoPaymentId() {
        return iyzicoPaymentId;
    }

    public void setIyzicoPaymentId(String iyzicoPaymentId) {
        this.iyzicoPaymentId = iyzicoPaymentId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
