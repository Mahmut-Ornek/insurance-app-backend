package com.company.insurance.document_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ReceiptCreateRequest(
        @NotNull Long collectionPaymentId,
        @NotNull Long applicationId,
        @NotBlank String customerEmail,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String currencyCode,
        boolean emailSent
) {}