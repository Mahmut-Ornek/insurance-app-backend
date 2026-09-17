package com.company.insurance.payment_service.dto;

import java.math.BigDecimal;

public record CollectionSummaryDto(
        Long id,
        BigDecimal totalAmount,
        BigDecimal collectedAmount,
        boolean isClosed,
        boolean isDeleted
) {
    public BigDecimal remainingAmount() {
        return totalAmount.subtract(collectedAmount);
    }
}