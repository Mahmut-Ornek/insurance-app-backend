package com.company.insurance.collection_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentSummaryResponse(Long id,
                                     Long collectionId,
                                     Long applicationId,
                                     BigDecimal amount,
                                     String currencyCode,
                                     LocalDateTime paidAt,
                                     String paymentReference) {
}
