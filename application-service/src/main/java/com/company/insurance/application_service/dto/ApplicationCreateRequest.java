package com.company.insurance.application_service.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationCreateRequest(@NotNull Long customerId,
                                       @NotNull Long productId,
                                       @NotNull Integer month,
                                       @NotNull Integer year) {
}
