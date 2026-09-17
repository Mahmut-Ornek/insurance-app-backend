package com.company.insurance.collection_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CollectionCreateRequest(@NotNull Long applicationId, @NotNull @Positive BigDecimal totalAmount,
                                      @NotBlank String currencyCode) {
}
