package com.company.insurance.application_service.dto;

import com.company.insurance.application_service.enums.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ApplicationResponse(Long applicationId, Long customerId, Long productId, ApplicationStatus status,
                                  BigDecimal calculatedPrice, String currencyCode, String description,
                                  LocalDateTime appliedAt, LocalDateTime decidedAt, String decidedBy) {
}
