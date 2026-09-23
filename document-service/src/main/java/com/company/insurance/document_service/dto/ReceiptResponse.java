package com.company.insurance.document_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceiptResponse(
        Long id,
        Long collectionPaymentId,
        String receiptNumber,
        Long applicationId,
        String customerEmail,
        BigDecimal amount,
        String currencyCode,
        boolean emailSent,
        LocalDateTime generatedAt
) {}