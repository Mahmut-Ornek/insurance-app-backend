package com.company.insurance.parameter_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdatePricingFactorRequest(@NotNull @DecimalMin(value = "0.0", inclusive = false)BigDecimal multiplier,
                                         String description) {
}
