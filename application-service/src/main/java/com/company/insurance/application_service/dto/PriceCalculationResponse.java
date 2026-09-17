package com.company.insurance.application_service.dto;

import java.math.BigDecimal;
import java.util.Map;

public record PriceCalculationResponse(Long customerId,
                                       Long productId,
                                       BigDecimal basePrice,
                                       BigDecimal finalPrice,
                                       String currencyCode,
                                       Map<String, BigDecimal> appliedMultipliers) {
}
