package com.company.insurance.insurance_app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record HealthResponse(
        Long healthId,
        Long customerId,
        boolean isSmoking,
        String smokingFrequency,
        BigDecimal height,
        BigDecimal weight,
        String bloodType,
        String alcoholConsumption,
        boolean hasChronicDisease,
        boolean hasPastSurgeries,
        String surgeryDetails,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime updatedAt,
        String updatedBy,
        boolean isDeleted,
        List<HealthDiseaseItemDto> diseases
) {}