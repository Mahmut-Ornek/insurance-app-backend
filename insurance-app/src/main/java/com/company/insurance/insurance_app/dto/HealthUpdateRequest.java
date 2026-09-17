package com.company.insurance.insurance_app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record HealthUpdateRequest(
        @NotNull Boolean isSmoking, @Pattern(regexp = "^(LIGHT|MODERATE|HEAVY)$") String smokingFrequency,
        @NotNull @Positive BigDecimal height, @NotNull @Positive BigDecimal weight,
        @NotBlank @Pattern(regexp = "^(NONE|OCCASIONAL|REGULAR|HEAVY)$") String alcoholConsumption,
        @NotNull Boolean hasChronicDisease, @NotNull Boolean hasPastSurgeries, String surgeryDetails,
        String updatedBy, @Valid List<HealthDiseaseItemDto> diseases
) {}