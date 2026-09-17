package com.company.insurance.insurance_app.dto;

import java.time.LocalDateTime;

public record DiseaseResponse(Long diseaseId, String code, String name, short severityScore, String description,
                              String updatedBy, LocalDateTime updateDate) {
}
