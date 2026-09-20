package com.company.insurance.document_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipt")
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long collectionPaymentId;

    @Column(nullable = false, unique = true)
    private String receiptNumber;

    @Column(nullable = false)
    private Long applicationId;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private boolean emailSent;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCollectionPaymentId() { return collectionPaymentId; }
    public void setCollectionPaymentId(Long collectionPaymentId) { this.collectionPaymentId = collectionPaymentId; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}