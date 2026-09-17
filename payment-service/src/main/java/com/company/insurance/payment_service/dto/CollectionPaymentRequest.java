package com.company.insurance.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CollectionPaymentRequest(BigDecimal amount, LocalDateTime paidAt, String paymentReference) {
}
