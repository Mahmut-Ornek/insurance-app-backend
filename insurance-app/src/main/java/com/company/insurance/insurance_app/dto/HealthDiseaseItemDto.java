package com.company.insurance.insurance_app.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record HealthDiseaseItemDto(
        @NotNull Long diseaseId,
        @NotNull LocalDate diagnosisDate,
        String details
) {}