package com.company.insurance.document_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSummaryDto(
        Long id,
        Long collectionId,
        Long applicationId,
        BigDecimal amount,
        String currencyCode,
        LocalDateTime paidAt,
        String paymentReference
) {}