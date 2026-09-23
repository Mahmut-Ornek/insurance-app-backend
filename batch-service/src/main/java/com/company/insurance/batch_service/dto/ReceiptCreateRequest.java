package com.company.insurance.batch_service.dto;

import java.math.BigDecimal;

public record ReceiptCreateRequest(
        Long collectionPaymentId,
        Long applicationId,
        String customerEmail,
        BigDecimal amount,
        String currencyCode,
        boolean emailSent
) {}