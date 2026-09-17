package com.company.insurance.insurance_app.dto;

import jakarta.validation.constraints.NotNull;

public record PriceCalculationRequest(@NotNull Long customerId, @NotNull Long productId,
                                      @NotNull Integer month, @NotNull Integer year) {
}
