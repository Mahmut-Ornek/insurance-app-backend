package com.company.insurance.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record ProductRequest(@NotBlank String name,
                             String description,
                             String createdBy,
                             @NotNull @Past LocalDate createDate,
                             String updatedBy) {
}
