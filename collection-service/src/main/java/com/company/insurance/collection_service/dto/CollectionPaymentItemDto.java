package com.company.insurance.collection_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CollectionPaymentItemDto(@NotNull BigDecimal amount, LocalDateTime paidAt,
                                       @NotBlank String paymentReference) {
}
