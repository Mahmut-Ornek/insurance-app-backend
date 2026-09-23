package com.company.insurance.policy_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PolicyCreateRequest(@NotNull Long applicationId, @NotNull @Positive BigDecimal premiumAmount,
                                  @NotBlank String currencyCode) {
}
