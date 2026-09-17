package com.company.insurance.payment_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentInitRequest(@NotNull Long collectionId, @NotNull @Positive BigDecimal amount, String currencyCode) {
}
