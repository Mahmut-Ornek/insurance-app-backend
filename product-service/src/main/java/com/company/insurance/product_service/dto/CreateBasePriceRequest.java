package com.company.insurance.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateBasePriceRequest(@NotNull Long productId,
                                     @NotNull BigDecimal basePrice,
                                     @NotBlank @Pattern(regexp = "EUR|GBP|TRY|USD", message = "Geçersiz para birimi kodu") String currencyCode,
                                     @NotNull Short month,
                                     @NotNull Short year,
                                     String createdBy) {
}
