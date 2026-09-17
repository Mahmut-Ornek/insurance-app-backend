package com.company.insurance.parameter_service.dto;

import java.math.BigDecimal;

public record PricingFactorResponse(Long id, String factorType, BigDecimal minValue, BigDecimal maxValue,
                                    BigDecimal multiplier, String description) {
}
