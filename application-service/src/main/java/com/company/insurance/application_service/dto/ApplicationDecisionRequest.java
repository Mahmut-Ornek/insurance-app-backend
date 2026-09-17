package com.company.insurance.application_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplicationDecisionRequest(@NotBlank String decidedBy, String description) {
}
