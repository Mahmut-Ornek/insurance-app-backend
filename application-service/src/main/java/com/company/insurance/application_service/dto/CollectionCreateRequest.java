package com.company.insurance.application_service.dto;

import java.math.BigDecimal;

public record CollectionCreateRequest(Long applicationId, BigDecimal totalAmount, String currencyCode) {
}
