package com.company.insurance.policy_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PolicyResponse(Long id,
                             Long applicationId,
                             Long customerId,
                             Long productId,
                             String policyNumber,
                             BigDecimal premiumAmount,
                             String currencyCode,
                             LocalDate startDate,
                             LocalDate endDate,
                             String status,
                             LocalDateTime issuedAt) {
}
