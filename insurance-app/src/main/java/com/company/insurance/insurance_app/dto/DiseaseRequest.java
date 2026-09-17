package com.company.insurance.insurance_app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record DiseaseRequest(@NotBlank String code, @NotBlank String name,
                             @NotNull @Min(1) @Max(10) Short severityScore,
                             String description, String updatedBy) {
}
